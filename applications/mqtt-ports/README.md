<!-- (c) https://github.com/MontiCore/monticore -->
# Basic Input Output

This example shows how to use MQTT to connect components to each other.
Architecture-wise it uses the same models and hand-written code as the 
Basic-Input-Output example:

<img src="docs/BasicInputOutput.png" alt="drawing" height="200px"/>

In the configuration, we however added two parameters to modify the generation:
```
<splitting>LOCAL</splitting>
<messageBroker>MQTT</messageBroker>
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
