# Log Tracing

In oder to use the log tracing feature make sure log tracing is enabled in the pom.xml of the application.

The middleware serves as proxy between the frontend and the application instances. 
It starts a crow webserver which is used by the frontend.
Communication with the application instances is done over predefined topics using MQTT or DDS.

## Usage
Start the binary or docker container with the following arguments:

- `message-broker` [MQTT/DDS] (make sure that the MontiThings application uses the same message broker)
- `DCPSConfigFile` [dcpsconfig.ini] (DDS configuration should match the config of the MontiThings application. Mixed transports such as UDP and TCP will not work)
- `DCPSInfoRepo` [localhost:12345] (In case DDS is configured using a discovery service)

The frontend is started using the docker container, or `yarn serve`. It uses port 3000 by default. 
