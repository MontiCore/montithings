<!-- (c) https://github.com/MontiCore/monticore -->
# IoT Client: Documentation
The IoT client communicates via MQTT with a deployment server or a "basic deployment target provider" on the deployment server.

This server manages the IoT clients and sends them commands.

## Client ID
Each IoT client has a unique ID.
This can be set manually if desired;
However, a MAC address of the executing computer is selected by default.
The deployment server can use this ID to address the individual clients individually.

## Client Location
The location of an IoT client is described by means of the triple <b>building, floor & room</b>.
Each component of the triple is an arbitrary string.

## MQTT Topics
| Topic                             | Sender          | Beschreibung                    |
|-----------------------------------|-----------------|---------------------------------|
| deployment/<i>clientID</i>/status | Client | Status-Updates (see below)                    |
| deployment/<i>clientID</i>/config | Client | Hardware configuration of the client (see below) |
| deployment/<i>clientID</i>/heatbeat| Client| Heartbeat for the server watchdog        |
| deployment/<i>clientID</i>/push   | Server | Instructions for deployment to client    |
| deployment/poll                   | Server | see below                              |

One MQTT topic is subscribed to by all IoT clients: <b>deployment/poll</b>.
If an IoT client receives a message on this topic, it sends its current status on its associated topic.

## Status
The status of IoT clients is sent in a simple JSON message. Example:
```json
{"status":"idle"}
```

## Heartbeat
The IoT client sends a heartbeat at regular intervals to signal that it is still online and available.
This is an empty MQTT message to the heartbeat topic of the client.

If no heartbeat is received from the client for too long, it is considered offline by the deployment server watchdog and removed from the deployment.

## Push
The deployment server can send a push message to the IoT client to start new components or stop running components.
To do this, it sends a message with a Docker Compose file.
The client reads this file using ```docker-compose``` and adjusts the active deployment accordingly.

## Docker Compose
A Docker-Compose file describes <b>all</b> components that are to be executed simultaneously on <b>a</b> client as a Docker container.
In doing so, ```docker-compose``` can determine the differences between a previous Docker-Compose file and a new one and apply only it.

Example of a Docker-Compose file. Three components are launched on this client.
```yaml
version: '3.7'
services:
  hierarchy.IOTest.tempSensor:
    command: --name hierarchy.IOTest.tempSensor [...]
    image: registry.git.rwth-aachen.de/.../hierarchy.temperaturesensor
    network_mode: host
    restart: always
  hierarchy.IOTest:
    command: --name hierarchy.IOTest [...]
    image: registry.git.rwth-aachen.de/.../hierarchy.iotest
    network_mode: host
    restart: always
  hierarchy.IOTest.avg:
    command: --name hierarchy.IOTest.avg [...]
    image: registry.git.rwth-aachen.de/.../hierarchy.averager
    network_mode: host
    restart: always
```
