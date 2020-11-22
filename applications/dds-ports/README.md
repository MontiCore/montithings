# Basic Input Output (DDS version)

This example shows how to leverage the Data Distribution Service (DDS) standard to connect components to each other.
Architecture-wise it uses the same models and hand-written code as the 
Basic-Input-Output example:

<img src="docs/BasicInputOutput.png" alt="drawing" height="200px"/>

In the configuration, we however added two parameters to modify the generation:
```
<splitting>LOCAL</splitting>
<messageBroker>DDS</messageBroker>
```

Since `splitting` is set to `LOCAL` (default is `OFF`) the generator will create 
multiple independent applications from the code. 
These applications are intended to be executed on a single machine.  
Secondly, `messageBroker` set to `DDS` specifies that the generated 
applications will use the [DDS standard][dds] to enable the applications 
to communicate. 
In our case, we use [OpenDDS][opendds] for that purpose. 

The applications will use DDS for both management traffic and data traffic.
Composed components tell their subcomponents to which other ports their ports
are connected.
This is done using the DDS topic `connectors`, similar to the MQTT integration.
A non-atomic component will publish all connections of its sub-components. 
In this case the `Example` component publishes a message on topic `connectors` with the following content:

`hierarchy.Example.source.value/out->hierarchy.Example.sink.value/in`

Each component subscibes the `connectors`-topic. 
If they find themselves at the receiving side within the message (after `->`), they will add a new INCOMING Port which subscibes to the OUTGOING port of the sending component.
Thus, component `Sink` subscribes to the topic `hierarchy.Example.source.value/out`.

The data is exchanged using the topics associated with the component and the its port.
In this case, `Source` publishes on topic `hierarchy.Example.source.value/out`.

Internally, management ports are configured with a more strict QoS policy:
Written data is kept in memory and every subsciber receives all messages. 
This allows late joiners to receive previously published connection information.


## Participant Discovery Configuration
In DDS, components communicate directly with each other. 
This means, that they have to find each other first.

OpenDDS offers a centralized discovery mechanism, a peer-to-peer discovery mechanism (DDSI-RTPS),
and a static discovery mechanism. 
As multicast might fail in a distributed environment, the default configuration leverages a discovery service which is shipped with OpenDDS.

In order to use the service, start the corresponding docker container and adjust the `-DCPSInfoRepo` argument of each component within `run.sh`.

//TODO!

## Installation and running the Example


1. Clone/Download and build [OpenDDS][opendds]
2. Start the Docker container which runs the discovery service
3. OpenDDS generated a `setenv.sh` script in the OpenDDS root directory. Execute `source setenv.sh`.
4. Run `run.sh`

[dds]: https://www.dds-foundation.org/
[opendds]: https://opendds.org/