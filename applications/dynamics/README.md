<!-- (c) https://github.com/MontiCore/monticore -->
# Dynamics

This example shows how to *dynamically* connect components to each other.
This means that components get connected at runtime instead of at design time.
To demonstrate this, we create a modified version of the basic-input-output example.
In case you haven't read this example yet, please first read the basic-input-output example.

<img src="../../docs/Dynamics.png" alt="drawing" height="400px"/>

In this modified version, the `Source` component is not a subcomponent of the `Example` component.
Instead, the `Source` component will be instantiated dynamically, i.e., at runtime, and the 
`Example` component will only then get the information how to connect to the `Source`.

For every  component with ports, i.e. a non-empty interface, MontiThings generates a class with the 
name schema `"Co" + Component Type Name`. 
In this case, we for example have a `CoSource`.
The same also holds for interface components, i.e., components without behavior. 
Since the `Source` component implements the `DataProvider` interface, it can also be connected in 
places where a component expects a `DataProvider` instead of a `Source` (this 
called "[Liskov's substitution principle][liskov]").

In this example, the `Example` component expects a `DataProvider`.
If it gets one, it connects it to its `Sink` component using the connect statement in the 
behavior block:
```
component Example {
  port in CoDataProvider dp;
  Sink sink;

  behavior dp {
    dp.value -> sink.value;
  }
}
```

After generating the code (using the usual `build.sh` script), we can start the non-dynamic 
components using the `run.sh` script. 
Next we want to instantiate a new component of type `Source`. 
We give it the (randomly chosen) instance name `newton`. 
Further, we ask it to print a message that we can then manually send to the `Example` component to 
inform it about the `Source`'s existence.
The message shall identify the `Source` using the `CoDataProvider` type that the `Example` expects:
```
./hierarchy.Source --name newton --printConnectStrCoDataProvider
```

Before starting the component, the `Source` will print out the message:
```
INFO: 2021-11-21 15:10:25,387 CoDataProvider Connection String: "{\"value0\":{\"payload\":{\"data\":{\"value0\":{\"value0\":\"newton.value\"}},\"nullopt\":false},\"uuid\":\"4f6b3ef7-06f3-4fa2-90c9-8521353bd3af\"}}"
```


Looking at the output of the `Example` component (`tail -f hierarchy.Example.log`), 
we see that it's not processing any messages up to now. 
We can also find the `portInject` topic for manually sending messages to a port: 
```
DEBUG: 2021-11-21 15:11:20,727 Connected to MQTT topic /portsInject/hierarchy/Example/dp
```

Using these two information, we can send a message with the interface of the `Source` component to 
the`dp` port of the `Example` component:
```
mosquitto_pub -t "/portsInject/hierarchy/Example/dp" -m "{\"value0\":{\"payload\":{\"data\":{\"value0\":{\"value0\":\"newton.value\"}},\"nullopt\":false},\"uuid\":\"5f5a6061-d2f1-4f51-a7b9-4c879da4097d\"}}"
```

Looking again at the log of the `Example` component, we see that the `Sink` component starts 
logging the values of the `Source` component, i.e., the components were successfully connected.



[liskov]: https://en.wikipedia.org/wiki/Liskov_substitution_principle
