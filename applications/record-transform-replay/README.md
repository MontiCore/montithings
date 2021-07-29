<!-- (c) https://github.com/MontiCore/monticore -->
# Record-Transform-Replay

A simple application adapted from the sensor-actuator-access example.

## Deployment

Unfortunately the recording module is not very flexible in terms of the used message broker and split flavor.
It's designed for a specific purpose, namely recording distributed application which communicate peer-to-peer (hence no MQTT) with each other.
This is why while recording, the application has to be configured as follows:

```
<splitting>DISTRIBUTED</splitting>
<messageBroker>DDS</messageBroker>
<recording>ON</recording>
<replayMode>OFF</replayMode>
<replayDataPath>${basedir}/target/generated-test-sources/recordings.json</replayDataPath>
```
 
### Recording

Configure the `pom.xml` accordingly: `cp pom_recording.xml pom.xml`.
Generate (`mvn clean install`), build (`./build.sh`/`./dockerBuild.sh`) and run the application as usual (`./run.sh`/`./dockerRun.sh`).
If build locally without docker make sure OpenDDS is sourced: `source <PATH_TO_OPENDDS>/setenv.sh`.

For some reason the docker container running the DCPSInfoRepo instance which ensures that all instances can find each other (also in WAN networks), communicates painfully slow.
The bootstrap process may take a minute.
Since no fix is known yet, it is advised to run it locally. 
Make sure to source OpenDDS, then execute `DCPSInfoRepo -ORBListenEndpoints iiop://localhost:12345` before running the application.
`./run.sh` will yield an error stating that the port is already allocated, this is okay.

When the app is running start the recorder tool:

- Make sure the tool is built: `cd ../../../../services/recorder && ./dockerBuild.sh && cd -`
- Start it: `../../../../services/recorder/dockerRun.sh --minSpacing 5 --fileRecordings recordings.json -n 1`
- Stop it using `Ctrl+c`. There should be a `recordings.json` file after the recorder stopped. When docker is used check if the file is owned by the current user, otherwise `chown` it.
- Move the recordings file: `mv recordings.json ../../`
- Stop the application: `./dockerStop.sh`

### Replaying

Configure the `pom.xml` accordingly: `cp pom_replaying.xml pom.xml`.
This changes the options as follows:
```
<splitting>OFF</splitting>
<messageBroker>OFF</messageBroker>
<recording>OFF</recording>
<replayMode>ON</replayMode>
```

Note that no DDS is used anymore and that it will output a single binary. 
This way, usual debugging tools can be used while replaying the distributed application including all environmental influences from the physical world.

Again, generate (`mvn clean install`), build (`./build.sh`/`./dockerBuild.sh`).
The generation step will now add multiple components dedicated for replaying purposes.

Run the application as usual, but make sure the `recordings.json` file is in the working directory: 
`./target/generated-sources/build/bin/hierarchy.Example -n hiearachy.Example`

It should now replay the original execution.