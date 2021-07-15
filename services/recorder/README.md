# Recorder

The recorder is a central entity which captures exchanged data between components.
Data includes network delay, non-deterministic behavior (e.g. system calls like `rand()`), and computation latencies.
After recording, the recorder processes collected data and makes small adjustments to the timing.
Resulting system traces stored in a JSON file and are used transform the model and replay the original execution.

## Building

Scripts are provided to build the recorder.
Use `dockerBuild.sh` to build a docker container, otherwise execute `build.sh`.
Make sure that OpenDDS is sourced in the latter case (`source OpenDDS/setenv.sh`).

## Usage

The recorder can be started manually (`./recorder`), or within a docker container (`./dockerRun.sh`).
If docker is not use make sure OpenDDS is sourced: `source OpenDDS/setenv.sh`.

You can configure the recorder as follows:

- `--DCPSConfigFile`: DDS config file. Make sure the same config is used in the application. (defaults to dcpsconfig.ini)
- `--DCPSInfoRepo`: DCPSInfoRepo host in case DDS has to find participants using a discovery service
- `--stopAfter`: Stop recording after given minutes.
- `--minSpacing`: Minimum spacing in ms between each message sent to the same component. (defaults to 0)
- `--fileRecordings`: File name where recordings are saved (defaults to `recordings.json`).
- `--n`: Number of outgoing ports of the application. When defined, the recorder will wait until all of these ports are connected to the recorder. (defaults to 1)

Configure the application as explained in the sample application TODO

## Known issues

- Yet, only the DDS message broker is supported.
- The use of "publish <port>;" breaks the measurement of the required computational time. It should not be used in recorded applications.
- Large applications crashed DDS entities for some reason. Root causes are still unclear. 