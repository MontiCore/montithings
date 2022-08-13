<!-- (c) https://github.com/MontiCore/monticore -->
# FaceUnlock - FaceID Door Opener with Google's "Protocol Buffers"

This example shows how to set up a pipeline with components using different high-level-programming-languages. 
Additionally, by using the protobuf-generator "cd2proto", we leverage the language-spanning capabilities of the
[Google-Protobuf-Language](https://developers.google.com/protocol-buffers) ([Github](https://github.com/protocolbuffers/protobuf)).

The elegance of retrieving a resembling `FaceUnlock.proto` definition out of a descriptive class diagram (i.e. `src/main/resources/models/unlock/FaceUnlock.cd`)
can be found in generating all necessary type safe bindings for arbitrary (yet *supported*) languages,
with a fast, memory efficient and stable implementation.
This example shows the communication between montithings-behaviour, a python implementation and a C++ implementation.
The montithings-behaviour is defined in 

<img src="../../docs/FaceUnlock-MontiThings.png" alt="drawing" width="1053px"/>

We will also show how a language can be integrated into the montithings-Code-Generator "montithings2cpp" 
at the example of the duck-typed language [Python](https://python.org), but also
what guidelines and design patterns may be followed to integrate a new language with the Generator.

## Getting started
To get the example up and running generate the code:
```bash
mvn clean install
```
and build the binaries for all compiled components:
```bash
cd target/generated-sources
./build.sh
```
After the C++-code and proto-code are built, and the python files are in place, make sure you have an MQTT-Broker running.
You could use mosquitto, if you don't have any.
```bash
# check if you have mosquitto running and start (if not running already)
systemctl info mosquitto.service
systemctl start mosquitto.service

# or run it locally in another terminal:
systemctl stop mosquitto.service # (if the service is running)
mosquitto                        # starts an MQTT-Broker on localhost

# listen to all MQTT-Topics:
mosquitto_sub -v -t "#"
```
After you made sure there is an MQTT-Broker that can be used for communication, start all components:
```bash
cd build/bin
./run.sh
```
You may observe the component-logs with tail. Omit "-f", if you just want a peek into logs.
```bash
tail -f unlock.FaceUnlock.camera.log # Montithings-Behaviour
tail -f python/FaceID.log            # Python-Behaviour
tail -f unlock.FaceUnlock.door.log   # C++-Behaviour
```
To stop the processes

This code generation is built for/with the following maven configuration parameter
```xml
<splitting>LOCAL</splitting>
<messageBroker>MQTT</messageBroker>
<serialization>Protobuf</serialization>
```

Since `splitting` is set to `LOCAL` (default is `OFF`) the generator will create 
multiple independent applications from the code. 
These applications are intended to be executed on a single machine.  
Secondly, `messageBroker` set to `MQTT` specifies that the generated 
applications will use a [MQTT message broker][mqtt] to enable the applications 
to communicate. 
In our case, we use [Eclipse Mosquitto][mosquitto] for that purpose. 
Therefore, before starting the applications you must install and start 
Mosquitto on your local machine (use `mosquitto &` for starting Mosquitto). 

The applications will use MQTT for both management traffic and data traffic.
Composed components tell their subcomponents to which other ports their ports
are connected.
This is done using the MQTT topics that start with `/connectors/`. 

Let's try that out by playing [man-in-the-middle][mitm]: 
Open a terminal window and start listening to all topics starting with 
`/connectors/`: 
```
mosquitto_sub -v -t '/connectors/#'
```
In a second terminal window, build the application and start only the 
`hierarchy.Example` component:
```
./build.sh hierarchy.Example/
cd build/bin
./hierarchy.Example hierarchy.Example 30006 30007
```

The numbers 30006 30007 are normally provided by the `run.sh` script and tell 
MontiThings which network ports to use in case there's no MQTT. 
In the first terminal window you should now see something like this:
```
/connectors/hierarchy/Example/sink/value hierarchy/Example/source/value
```
That's the connector you can see in the picture above. 
The port with the name fully qualified name `hierarchy/Example/sink/value` is 
told to get its data from the port with the fully qualified name 
`hierarchy/Example/source/value`. 


Next, lets try to look at how the data flows through MQTT. 
The data is exchanged using the topics that start with `/ports/`
In your first terminal window, listen to those topics:
```
mosquitto_sub -v -t '/ports/#'
```
Next, start all of the applications as usual using your second terminal window:
```
./run.sh
```

Now, you should see the messages exchanged by the components running through 
the first terminal window:
```
/ports/hierarchy/Example/source/value {
    "value0": 1
}
/ports/hierarchy/Example/source/value {
    "value0": 2
}
/ports/hierarchy/Example/source/value {
    "value0": 3
}
```
As you can see the `hierarchy/Example/source/value` port sends increasing 
numbers. Great!

Now stop the application in the second terminal window:
```
./kill.sh
```
In the first terminal window you should now see that there are no new messages 
coming in.

If you provide the hostname and port number of the MQTT broker to the 
application when starting it, it is also possible to use MQTT brokers located on
different machines, and thus, to let distributed components communicate.


[mqtt]: https://en.wikipedia.org/wiki/MQTT
[mitm]: https://en.wikipedia.org/wiki/Man-in-the-middle_attack
[mosquitto]: https://mosquitto.org/

# Design decisions