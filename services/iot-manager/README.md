<!-- (c) https://github.com/MontiCore/monticore -->
# IoT Manager

## General
The IoT Manager takes care of the internal orchestration of the deployment.
It can communicate and be controlled via HTTP and MQTT.
In the original use case, it communicates with a [MontiGem-Backend (not yet publicly available)](https://git.rwth-aachen.de/se-student/theses/ba-schneider_philipp/exampleapplication).

Alternatively, the Deployment Manager can also be integrated into other Java applications. 
To do this, consider the entry point of the IoT Manager.

## Kubernetes (k8s / k3s)
Including Kubernetes nodes in the deployment requires a service account.
The following script creates such an account, configures its access rights, and then outputs the access token.

Note that this script must be run on an authorized node; preferably a master.

```bash
cd ./setup/kubernetes
./setup.sh
```

The following additional information can be assigned to nodes via labels:
1. Their location (building, floor, room) <b>must</b> be specified.
2. Hardware <b>may</b> be specified via labels whose key starts with "hardware".

There is also a script for this, which must also be executed on an authorized node. This script requests the necessary information bit by bit.
```bash
cd ./Setup/kubernetes
./setupClient.sh
```
```
Node-ID: iot4
Please enter the location of the node.
Building: 001
Floor: 002
Room: 003
Please enter the available hardware on the node. When finished, submit empty line.
Hardware: actuatorTemperature
Hardware: 
The node iot4 is located in building 001 on floor 002 in room 003.
It is equipped with the following hardware:
- actuatorTemperature
Is this correct? (Yes = Press enter, No = Terminate)
 
Applying configuration...
[...]
Client has been configured successfully.
```

<b>Note:</b> The scripts are designed for k3s. If another kubectl should be used, this can be adapted by settings the variable `$KUBECTL`.

## GeneSIS
To include GeneSIS hosts in the deployment, the API endpoint GeneSIS <b>Engine</b> can be used.
Only hosts that meet the following criteria are considered for deployment:
1. The host is of type "/infra/device", so it is accessed via SSH.
2. The `device_type` property of the host is a JSON formatted string with (at least) the following properties ("hardware" is optional):
```JSON
{
    "building": "[...]",
    "floor": "[...]",
    "room": "[...]",
    "hardware": [
        "sensorTemperature"
    ]
}
```

To register the hosts, the graphical interface of GeneSIS can be used.
There you can add a host under "Infrastructure Components" > "Device".
Once you have added the desired devices, you can use <i>Push model to server</i> <b>and</b> <i>Deploy model on server</i> to apply the changes.

Note: If you want to make changes to a component, the version number of this component must be incremented, otherwise changes may not be applied.
