<!-- (c) https://github.com/MontiCore/monticore -->
# FaceUnlock - FaceID Door Opener with Google's "Protocol Buffers"

This example shows how to set up a pipeline with components using different 
high-level-programming-languages. 
Additionally, by using the protobuf-generator "cd2proto", we leverage the language-spanning
capabilities of the
[Google-Protobuf-Language](https://developers.google.com/protocol-buffers)
([GitHub](https://github.com/protocolbuffers/protobuf)).

The elegance of retrieving a resembling `FaceUnlock.proto` definition for a descriptive
class diagram (i.e. `src/main/resources/models/unlock/FaceUnlock.cd`)
can be found in generating all necessary type safe bindings for arbitrary (yet *supported*)
languages,
with a fast, memory efficient and stable implementation.
This example shows the communication between montithings-behaviour, a python implementation 
and a C++ implementation.
The montithings-behaviour is defined in `src/main/resources/models/unlock/Camera.mt`,
while the C++- and Python-implementations can be found in `src/main/resources/hwc/unlock`. 

<img src="../../docs/FaceUnlock-MontiThings.png" alt="drawing" width="1053px"/>

> **Simplified**: The general idea of this example MontiThings-application is to have an 
> IoT-Application that will take a picture of a person with a `Camera`.
> The `Image` will then be send to `FaceID`, which will identify if the visitor is allowed 
> to enter the `Door`. `Door` will therefore only open if the `Person` is authenticated by 
> `FaceID`.

To keep things simple, the example `Camera` will send an integer resembling a specific person, 
instead of an actual image (or byte-array).
Another approach could be to couple the `Image`-port to a Sensor-port 
(see Sensor-Actuator-Manager).
Also, in lack of a real world test-bench, the example `FaceID` makes a "database-lookup"
on a hash-map instead of actually querying a database.
And the example `Door` will only print to its log if a `Person` is allowed.
Regardless of these limitations, the user should be able to integrate this example into a real 
test-bench, when making the according modifications:

> **Advanced**: The `Camera` will capture a byte-array, send it to `FaceID`, 
> possibly a cloud-service, where a lookup on PostgreSQL, Keycloak, etc. will be done,
> and then send the result to the `Door`-Microcontroller-application, 
> where an actuator will open the lock.

We will also show how a language can be integrated into the montithings-Code-Generator
"montithings2cpp" at the example of the duck-typed language [Python](https://python.org).
We then derive guidelines and design patterns that may be followed to integrate a new 
language with the Generator.

## Getting started
### **Step 0**
- You will have to use `protoc`, Google's Protobuf-Compiler.
  We have developed this project with `protoc` version [3.12, 3.21).
- You will also have to install a protobuf-library, that can work with the `protoc`-version.
  Safe is a version equal to `protoc`, e.g. 3.12.x for `protoc`-v3.12
  As we use C++ and Python here, you will need both libraries installed
You can install compatible versions with 
the recommended montithings-install-script `installLinux.sh`
or install the dependencies manually
```bash
sudo apt install protobuf-compiler libprotobuf-dev python3-protobuf
```
You may install the newest release from 
[GitHub:protobuf/releases](https://github.com/protocolbuffers/protobuf/releases)

### **Step 1**
 To get the example up and running, generate the code:
```bash
mvn clean install
```
### **Step 2**
and build the binaries for all compiled components:
```bash
cd target/generated-sources
bash ./build.sh
```
After the C++-code and proto-code are built, and the python files are in place,
make sure you have an MQTT-Broker running.
You can use mosquitto.
```bash
# check if you have mosquitto running and start it, if not running already
systemctl info mosquitto.service
systemctl start mosquitto.service

# or run it locally in another terminal:
systemctl stop mosquitto.service # (if the service is running)
mosquitto                        # starts an MQTT-Broker on localhost

# listen to all MQTT-Topics:
mosquitto_sub -v -t "#"
```
### **Step 3**
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
To stop the processes simply execute in the same folder
```bash
./kill.sh
```

This code generation is built for/with the following maven configuration parameter
```xml
<splitting>LOCAL</splitting>
<messageBroker>MQTT</messageBroker>
<serialization>Protobuf</serialization>
``` 
## Modify behaviour - hand-written-code and interfaces
Hand written code, or "HWC", is user defined behaviour that interfaces with generated Files.
For this example you will find three HWC-files 
```bash
src/main/resources/hwc
- DoorImpl.cpp
- DoorImpl.h
- FaceIDImpl.py
```
You may find that HWC-Files match the component-name they provide behaviour for,
suffixed with an `Impl` and the language they should be run in.
In the package `unlock`, there are two components `Door` And `FaceID`.
- The `Door`-component will be generated and compiled to use C++-behaviour.
- The `FaceID`-component will be generated to use Python-behaviour.

Depending on the file extension you provide your code in, 
the generation, compilation and execution of the resulting component differs.
Providing HWC will also eliminate the need for a `behaviour {}`-Block 
in the `.mt`-component-definition.

> In the following we will look on Python-HWC, at the example of FaceID

The `FaceIDImpl` file (HWC) interfaces with the `FaceIDImplTOP` file,
which can be found after generating (Step 1) in `target/generated-sources/hwc`.
In the Python integration, the HWC implements the corresponding `ImplTOP`-class, which
  - needs an `__init__`-call that instantiates the FaceIDImplTOP-class with a client_id 
  - provides an MQTT-Connector `MQTTConnector` for in/out-ports 
  - enforces the `IComputable`-Interface, 
    so the user has to implement the 
    - `compute`-method, which is the entrypoint to manipulate in/out-ports,
       whenever a message on an in-port is received
    - *optional* `getInitialValues`-method, which is called when MQTT is connected, 
      to initialize the state of in/out-ports  

After providing HWC, the component is then encapsulated in the actual component `FaceID.py`,
which can also be found in `target/generated-sources/hwc`
In the case of Python this only takes the implementation of `FaceIDImpl` and starts
the `MQTTConnector` from `FaceIDImplTOP` with a `connect()`, respectively `paho.mqtt.client.MQTTClient.loop_forever()`.
On startup, the `MQTTConnector` will
- publish its given client_id [client.id] under topic `components` as a topic-string
  - i.e. `unlock.FaceUnlock.faceid` as topic-string `unlock/FaceUnlock/faceid`
- subscribes to `/connectors/[client/id]/[in-port]`, 
  to later subscribe to the topics on which the port should receive data
  - i.e. subscribe topic `/connectors/unlock/FaceUnlock/faceid/image`
- connect all out-ports to the appropriate topics under topic `/ports/[client/id]/[out-port]`
  - i.e. topic `/ports/unlock/FaceUnlock/faceid/visitor` for the `FaceID`-`visitor`-out-port

The component `FaceUnlock` will thus be notified, that a new component has subscribed
the MQTT-Broker.
It will then publish on the `/connectors/#`-topic, which in-ports should be subscribed
to what topics.

### Using ports in HWC
On setup there will also happen an instantiation of the fields
- `FaceIDImplTOP._input: FaceIDInput`
- `FaceIDImplTOP._result: FaceIDResult`. 
These hold the ports that the HWC has to interact with.
Because the symbols have to be matched against the subscribed in-port-topics,
the lookup has to be done dynamically, to avoid class-loading.
This lead to an implementation, such that the user has to access a port by string-lookup
like so:
```py
self._input.ports['image']
self._result.ports['visitor']
```
The ports themselves, however, are an instantiation of the compiled protobuf-definition.
So one may use the protobuf-provided methods like 
[documented for python](https://developers.google.com/protocol-buffers/docs/pythontutorial):
```py
image = self._input.ports['image']
visitor = self._result.ports['visitor']

visitor.visitor_id = image.person_id
```
Consecutive calls of `FaceID.compute()` were required to access previous state,
which one initializes with `FaceID.getInitialValues()`.
Therefore the implementation was required to retain state, which is done by providing
the stateful instances of `_input` and `_result`.
That way, when receiving on an in-port, the HWC may decide to use/save the state of
`_input.ports` so that when receiving on another in-port the previously set port may be used.
Also, as the `compute` method is now state-bound to the the `ImplTOP`-class, the argument
to the method is *not* a member of `FaceIDInput`, but rather the *string* that represents 
the port in `self._input.ports`.
As such, the HWC can identify the port from which `FaceIDInput` was last manipulated from.

Once the HWC has manipulated all fields and is ready to send, this can be done via
the method `FaceIDImplTOP.send_port_visitor()` for the out-port `visitor`.
As the out-ports are always using the same behaviour, these are generated statically
with the pattern `send_port_[out-port]`, and may be used for every out-port alike
for a side-effect-ful `publish` on every desired out-port.

### Serialization and Deserialization of in-/out-port-data
One key decision to why to use protocol buffers, is to send and receive serialized data,
that is automatically formulated into discrete data.
To parse and serialize into the protocol buffer format is one part of the pipeline.  
Yet, the deserialization on receiving on an in-port and serialization when sending to an 
out-port is done implicitly. 
The user does not, and should not have to interact with the de-/serialization-process.
Because we know which data type will be delivered to which port, when instantiating
`Input` and `Result`, the appropriate Protobuf-constructors are already in place.
Therefore, when receiving data on in-port `image`, this is directly translated to
be deserialized by the `Image()`-object.
Similarly, because the appropriate out-port `visitor` is manipulated directly,
when calling `send_port_visitor()`, the corresponding `Person()`-object, that holds
all `visitor`-information, is serialized implicitly when publishing. 

## Integrate another language to use Google's "Protocol Buffers"
Because using Google's "Protocol Buffers" and its compiler `protoc` already provides
us with the option to generate a language specific implementation for all types in our
Classdiagram, it is quite straight forward to integrate a new language.

However, the difficulty is to apply the respective design principles of MontiThings:
1. Handle communication with other services/components (over MQTT)
2. Handle all necessary symbols
   - Package names
   - ClientID (name of the instance)
   - Ports
   - Types
   - Imports
3. Generate and provide the Code that is needed, so that a HWC works with MontiThings
   - IComputable
   - MQTTConnector
   - ImplTOP (for example `FaceIDImplTOP`)
   - Client (for example `FaceID`)
4. Compile the generated Code
   - Bundle all generated Code, such that the compiler can build the binary
   - Handle imports and their respective paths *correctly* 
5. Run the Code, when executing the build-script
   - Handle log files 
6. Stop the Code, when executing the kill-script
7. language-specific quirks and design principles

Although not every feature in this guideline that is described below is implemented in the
the implementations, it contains a lot of lessons learned, that arose during the 
integration of Python, C++ and Go.
This guide specifically follows the threads on how to integrate python into MontiThings,
but should be generically applicable to new languages.

> Following, the instantiation of a component is referred to as a "service"

### Step 1. - Communication
The first step is to get to know the communication interface that the new language should use.
The MontiThings MQTT-Connector uses different mechanics to interface with a service.
#### Register Service
Every component should "sign in" when connecting to MQTT.
For that, the service will publish their respective symbol (e.g. `unlock/FaceUnlock/faceid`)
to the topic `/components`.
#### Dealing with connectors
This will also trigger the `FaceUnlock` service to publish the connectors.
The connectors will be published to the respective topics in the form of
```bash
# generic
/connectors/client/id/in-port other/client/id/out-port
# specific
/connectors/unlock/FaceUnlock/faceid/image unlock/FaceUnlock/camera/image
```
> Please make sure you subscribe to the in-port-topics *before* registering your service.

> It is possible, that one in-port may receive data from several out-ports. Handle them accordingly!

Every in-port should subscribe to the respective topics published on `/connectors`.
Every port-topic is of the format:
```bash
# generic
/ports/other/client/id/out-port
# specific
/ports/unlock/FaceUnlock/camera/image
```

#### Send to out-ports
In contrast to the in-ports, the out-ports are quite simple and static in their behaviour.
To send to another service, respectively to send to an out-port, our service publishes 
to the topic
```bash
# generic
/ports/client/id/out-port
# specific
/ports/unlock/FaceUnlock/faceid/visitor
```

### Step 2. - Symbols
#### Package, components, class diagram names
The MontiThings definition will use different symbols to identify different parts of the
definition.
Make sure you use the appropriate symbol for the task.
A fully qualified symbol will have different ways to access them in a GeneratorStep.
Watch out for 
- package names
- class diagram names
- component names
- variable names

#### ClientID - name of the service
The client id is handed to an executable service to distinguish it, for example via MQTT.
Be sure to not have different running instances with the same client id.
This will force the MQTT-Connector to disconnect/reconnect, when two services identify as
the same.

#### Ports and Types
The in- and out-ports are defined in the `.mt`-definitions (e.g. `FaceID.mt`).
Every port has a type, that is equivalent to the protobuf class for that specific port.
> In the current implementation of `cd2proto` package names are ignored!
  A proto class of `Image.Image()` will be provided as `Image()`.

#### Imports
In most languages imports resemble some kind of package or folder structure.
When dealing with imports always make sure, that a compiler/interpreter is able to find
all packages, and *don't* shadow imports by providing them on multiple paths.


### Step 3. - Generate Code
This step resembles the execution of `mvn clean install` in the appropriate `application` 
folder.
To generate code we use the `generators/montithings2cpp`-Generator, that provides the
infrastructure we need to embrace a new language.
> In the following we "generator" refers to `generators/montithings2cpp/src/main/java/montithings/generator` 
#### Generator Step
You will need to add a new GeneratorStep to the `MontiThingsGeneratorTool`.
You can put your code into `steps/generate`.
There you can also refer to `GenerateProtobuf` and `GeneratePythonHwcComponent`.
In our approach we used Freemarker Templates, or FTL, as they nicely resemble the
generated code, and even offers some Java-interop inside the FTL.
You can refer to `codegen/template/util/pythonComponent` for the Python `.ftl`-files.
- ComponentTOP.ftl - ImplTOP (for example `FaceIDImplTOP`)
- Component.ftl - Client (for example `FaceID`)

##### TOP
The TOP mechanism should provide everything needed for the hand-written code:
The instantiation of the Input and Result types, and the Computable interface.

Furthermore, the handling of in-ports and out-ports should be implemented here.
- A static implementation for out-ports is needed, e.g. by generating a `send_port_x`-method
- A dynamic implementation for in-ports is needed, that can handle the dynamic 
  connectors mechanism of in-ports
- encapsulate deserialization of in-ports 
- encapsulate serialization of out-ports

##### Component
The component is the entry-point to the service, and thus the main-function.
This should provide the actual compiled or interpreted entrypoint to execute in the
run-script.
Here, the MQTT-Connector is finally called and runs forever in a listening, subscribed state.

##### Libraries, static code and generic imports
Static code can be put in the `resources` folder.
For python, there is a dedicated folder with all necessary static imports:
- IComputable.py
- MQTTConnector.py

### Step 4. - build-script - compile the generated Code
The build-script is defined in `codegen/template/util/scripts/BuildScript.ftl`

Generating Code is one job, but bundling all generated Code, such that the compiler can 
build a binary from or interpret it correctly is not trivial.
Especially imports that depend on packaging or folder structure will make it *much harder*
to generate correctly imported code.
Especially generating code that depends on folder structure may be avoided to reduce
integration complexity.

The build-script should not generate any code, but instead compile and bundle code into the
`target/generated-sources/build/bin` directory.
Depending on your language implementation you should the `protoc`-call here.
Choose the appropriate `--<language>_out=` folder where you would like to output the
compile protocol buffer file for your language.
You can find the exact option to use when executing
```bash
protoc --help
# for python3 
protoc --python_out=hwc/.
```
Notice that you may need to install a plugin to use the protoc compiler with your language.
For example on the branch `lang/go` you would need to work with the appropriate go plugin.

### Step 5. - run-script - run the generated and compiled Code
The run-script is defined in `codegen/template/util/scripts/RunScript.ftl`

When adding the execution of a binary or interpreted code to the run-script, it is
necessary to explicitly set a client_id that can handle
The run-script should also make sure, that executed service writes all output, like logging,
errors, etc. into a discrete log-file.
The log-file follows the pattern `Component.log`.
For a python component-file `python/FaceID.py` the log-file would look like 
`python/FaceID.log`

### Step 6. - kill-script - stop the Code
The kill-script is defined in `codegen/template/util/scripts/KillScript.ftl`.

When adding the termination of all new processes to the kill-script,
one should *only* handle the termination of the respective process.
Generic kills, like `killall python3`, should be avoided as this can easily interfere
with other started processes.
As a rule of thumb, the specifications of the kill-script are similar to the run-script,
and may be set up similarly.

### Step 7. - language-specific quirks and design principles
Depending on the language you want to integrate, different quirks may arise.
For statically compiled code, like C++, the language will hinder you on integrating, 
whenever you have to specify a type.
For dynamic, interpreted languages, like Python, the language will provide a good amount 
of fault tolerance, when it comes to specifying types.
This on the other hand makes it much harder to spot implementation errors, and thus the 
generated code *must* be tested much more meticulous, before a generated implementation
is ready. 

## Design Decisions
#### json-encapsulation
@Sebastian
Although protobuf could be published solely as the protobuf-String, this interferes
with MontiThings' mechanism to monitor messages.
The protobuf string is instead encapsulated in a JSON-Format that MontiThings already
knows:
```json
{
    "value0": {
        "payload": {
            "nullopt": <nullopt>,
            "data": <base64-encoded-serialized-protocol-buffer>
        },
        "uuid": <message-uuid>
    }
}
```
> The base64-encoding has to happen, because the C++-Implementation for JSON crashes
  for null-Bytes in protobuf (not compatible with UTF-8)