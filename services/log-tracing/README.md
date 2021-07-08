# Log Filtering

In oder to use the log filtering feature make sure log tracing is enabled in the pom.xml of the application.
Refer to the sample application for a detailed instruction.

This directory includes sources of the middleware and the frontend.
The middleware serves as proxy between the frontend and the application instances. 
It starts a crow webserver which is used by the frontend.
Communication with the application instances is done over predefined topics using MQTT or DDS.

## Building

Scripts are provided to build the recorder.
Use `dockerBuild.sh` to build a docker container, otherwise execute `build.sh`.
Even though MQTT is used, make sure that OpenDDS is sourced in the latter case (`source OpenDDS/setenv.sh`).
The frontend is build using `yarn install`.

## Usage

Start the binary (`./logtracer_middleware`) or the docker container (`dockerRun.sh`) with the following arguments:

- `message-broker` [MQTT/DDS] (make sure that the MontiThings application uses the same message broker)
- `DCPSConfigFile` [dcpsconfig.ini] (DDS configuration should match the config of the MontiThings application. Mixed transports such as UDP and TCP will not work)
- `DCPSInfoRepo` [localhost:12345] (In case DDS is configured using a discovery service)

The frontend has to be started using `yarn serve` in case docker is not used. It uses port 3000 by default. 
