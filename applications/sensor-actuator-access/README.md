<!-- (c) https://github.com/MontiCore/monticore -->
# Sensor & Actuator Access

This example shows how to connect components to sensors and actuators or, more
generally speaking, hardware.
The example is based on the basic-input-output example but has two additional
ports:

<img src="../../docs/SensorActuatorAccess.png" alt="drawing" width="400px"/>

The additional unconnected ports can be used for accessing hardware such as 
sensors and actuators.

To do so, developers can either use Freemarker Templates or program external ports
other programming languages. Both methods require a MontiThings Config File for each
component type with ports which connect to the outside world. In this Config File,
the type of hardware the port should be connected to can be specified. 
The following code snippet specifies that the actuator port of sink components
should be connected to hardware which considers itself to be of "example-type".

```
config Sink for GENERIC {
    actuator {
        mqtt = "example-type";
    }
}
```


## Freemarker-based external Ports

MontiThings uses the templates to inject code in the ports.
To enable MontiThings to find the templates, you have to give them specific 
names and replace `<ComponentName>` and `<PortName>` with the names of the 
component and port you want to inject your code into: 
- To add import statements, use `<ComponentName><PortName>Include.ftl`
- To provide values to the component (i.e. implement an incoming port), 
use `<ComponentName><PortName>Provide.ftl`
- To process values from the component (i.e. implement an outgoing port), 
use `<ComponentName><PortName>Consume.ftl`
- To specify the type of the port use `<ComponentName><PortName>Type.ftl`
- To provide the type of hardware which the port should be for MontiThings
`<ComponentName><PortName>Topic.ftl`

For example, we can implement an actuator port like this: 
For the purpose of not requiring any hardware to run this example, we just print
the values to the console.
This is done by `TestActuatorConsume.ftl`:
```
if (nextVal)
  {
    std::cout << "Sink: " << nextVal.value () << std::endl;
  }
else
  { 
    std::cout << "Sink: " << "No data." << std::endl; 
  }
```
If there's a value at the port it prints "Sink: " followed by the value at the 
port. 
Otherwise (if the compute method does not write data to that port), we only 
print "Sink: No data.".
As this uses `std::cout` our C++ code also requires an include statement which
we add with the `TestActuatorInclude.ftl` template:
```
#include <iostream>
```

Accordingly, running this example will result in both of the actuators printing
increasing numbers:
```
Sink: 0
Sink: 1
Sink: 2
Sink: 3
Sink: 4
Sink: 5
Sink: 6
```

## External Ports in other programming languages

Sometimes C++ just isn't the right choice to access a sensor or actuator.
For example, if the sensor's manufacturer only provides a library in a
different language you probably don't want to port that library to C++.
MontiThings can talk to other applications using MQTT.
This allows MontiThings also to talk to small applications on the same
device as the component.

For Python, one of the most popular IoT languages besides C++,
MontiThings already offers a `montithingsconnector.py` script that
handles the communication with MontiThings.

To receive data from MontiThings in Python, developers shall implement
the `receive` callback. In the following example, messages which get received 
get printed:
```Python
from montithingsconnector import MontiThingsConnector as MTC


# Handle an incoming message by printing it (actuator port)
def receive(message):
    print(str(message, 'utf-8'))


# Set up MontiThings port connection
mtc = MTC("example-type", receive)
```

Note that to set up the MontiThings port connection, it has to be specified which
type of hardware is being offered to MontiThings by the specific implementation.
In the example above, this is "example-type". This would make it possible for
MontiThings ports which have the same string specified in their MontiThings Config
File to connect to this Python Script. For sending data, the process is similar, with
the distinction that no receive method has to be implemented.
The following example shows how to send a number which increases every two seconds:

```
from montithingsconnector import MontiThingsConnector as MTC
import time


if __name__ == '__main__':
# Set up MontiThings port connection
mtc = MTC("example-type-2", None)
i = 0
while True:
    # Send a message to MontiThings (sensor port)
    mtc.send(i)
    i = i + 1
    time.sleep(2)
```