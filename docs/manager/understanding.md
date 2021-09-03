# Understanding the IoT-Manager

The IoT-Manager consists of several components:

- REST API: A flask app administrates all data, stored in a sqlite database, which can be retrieved and updated through API endpoints.
- MQTT Communication: This interface is used by the clients. They register themselves and send heartbeats over MQTT. The manager updates the corresponding data and might push docker-compose.yaml files, in case a new distribution is computed.
- Distribution Calculator: A config file `config.json` is consumed which defines the distribution constraints. This file is manually written, according to the infrastructure needs. Based on this config, a distribution is calculated by solving the given constraints. Based on the distribution, docker-compose files are created for each affected device and eventually pushed to it.

The following sequence diagram shows a simplified process where two devices register at the manger and get back their docker-compose file.

![](/docs/img/sequence_basics.png)

1. Devices register at the manager with their configuration. See the client config [here](./configuration.md).
2. Based on the new world knowledge (what devices exist and how docker images should be distributed), the manager computes a distribution which device should run which docker containers.
3. For each device, new docker-compose.yaml files are generated -- based on this distribution.
4. These docker-compose.yaml files are pushed to the client which subsequently runs the docker-compose.yaml file


## MQTT Communication
The communication between manager and clients is realized via MQTT. Its use requires a so-called MQTT-Broker which acts as a central instance in the communication network and through which all messages are processed. An [Eclipse Mosquitto Broker](https://hub.docker.com/_/eclipse-mosquitto) is used which runs in its own docker container on the hardware of the manager.
This protocol allows the publication of data on a specified topic. All communication participants that follow this topic then receive the sent data.

- Both, manager and clients, implement a local MQTT client with which they log on to the MQTT Broker. Heartbeats are published by the clients on the topic 'topic/heartbeat' which is only subscribed by the manager.

- TODO: Abarbeitung von Heartbeats, Senden von docker-compose


## Distribution Calculator (Prolog Generation)

The basics of the configuration file are explained [here](./configuration.md).
When the manager is started it will read the `config.json` and process it as follows:

1. Generating `facts.json`, which is basically just a merge of all device configurations, extended with their states, i.e. whether they are online or not (this is checked via heartbeats). See [facts.json](/docs/examples/distribution_config/world/facts.json) as an example.
2. Generating [facts.pl](/docs/examples/distribution_config/world/facts.pl), which is the prolog version of `facts.json`.
3. Generating `query.pl`, which is based on `config.json`. It includes all necessary code (including `facts.pl` and [helpers.pl](/docs/examples/distribution_config/world/helpers.pl)). See e.g. [query.pl](/docs/examples/distribution_config/example1/query.pl) of `example1`.
4. Consulting `query.pl` and returning a valid distribution or an error otherwise.

When writing a `config.json` file (see [Configuration](./configuration.md)), the constraints are defined for each component.
It is further split into `distribution_selection` and `distribution_constraints`.
The corresponding Prolog code will first compute a set of possible clients (according to `distribution_selection`), and then apply the constraints on top of it.
Thus, the writer of `config.json` has to consider the following:

- `distribution_selection` will yield a list with the highest possible amount of clients which fit the requirements.
- ">=" operators (e.g. "at least 2 sensors should be online on floor 1") are checked against the list of `distribution_selection`.
Hence, as `distribution_selection` yields the largest list that is possible, the result might not be desired. If, for instance, there are 100 device candidates which is overfitted for the needs, then one should add constraints with "<=" operators.
- "<=" operators (e.g. "at most 2 sensors in floor 1") may compute multiple possible solutions. This can decrease performance.
- The Prolog program may not terminate and yet, there is no possibility to hint possible problems why no distribution can be computed. It works, does not terminate, or returns "false."


### Limitations

##### Impossible distributions may not terminate

- Description: Prolog does not always terminate.
- Mitigation: A timeout of several minutes will eventually decide that the distribution is infeasible.
- Current state: this scenario is not typical in this use case and is therefore ignored.

##### <= constraints are not performing well

- Description: Each constraint which further narrows down the set of feasible solutions may open new solution branches which effectively decreases performance
- Mitigation: Avoid multiple <= constraints
- Note: typical distribution configurations can be calculated efficiently
