<!-- (c) https://github.com/MontiCore/monticore -->
# Retain State

This example shows how components may retain their states over multiple runs 
or when failing unexpectedly. 
For simplicity, this example is mainly based on the basic-input-output example 
with the exception of the `RunningSum` component that calculates the running
total of its incoming values:

<img src="../../docs/RetainState.png" alt="drawing" height="200px"/>

There is, however one important difference in the `Source` component:
```
component Source {
  port out Integer value;

  int lastValue = 0;

  behavior {
    value = lastValue++;
  }

  retain state; // this line makes the component save and restore its state
}
```

The `retain state` statement tells MontiThings to save and restore the state
of the component automatically. 
This is done, however, only for things that are known in the model. 
So the `lastValue` variable will be (re)stored but everything that the user 
might add in hand-written code will not automatically be (re)stored.

Let's try that out. Generate the code as usual (`mvn clean install`) and compile
the source code unter `target/generated-sources` by calling 
`./build.sh hierarchy/`.
Now go to the folder with the binaries (`cd build/bin`). 
Starting the application (`./hierarchy.Example -n hierarchy.Example`) should 
given you the following output:
```
Component instance 'source' could not restore state from local file.
Component instance 'rs' could not restore state from local file.
Started.
Source: 0
Sink: 0
Source: 1
Sink: 1
Source: 2
Sink: 3
Source: 3
Sink: 6
^C
```

You can see that the `source` and `rs` component instances could not restore a 
state because this is the first run. 
Stop the application and start it again.
Now you should see the following output:
```
Started.
Source: 4
Sink: 10
Source: 5
Sink: 15
Source: 6
Sink: 21
^C
```
As you can see, MontiThings restored the state of the `Source` component, so the
output does not start with `0` but instead continous at `4` for the `Source` and
`10` for the `RunningSum`, meaning that both states could be restored. Also, #
note that the two log messages that inform us the state couldn't be restored are
not shown during the second run. 

## Simulate failing components

Next, let's look at the distributed case to simulate the unexpected failure of 
a component.
Comment in the following two lines in the `pom.xml`:
```
<splitting>LOCAL</splitting>
<messageBroker>MQTT</messageBroker>
```
This will cause MontiThings to build a distributed application. 
If you've never seen this you can have a look at the other examples in this 
repository and come back later or just ignore it for now.
Build and compile the application as usual:
```
mvn clean install
cd target/generated-sources
./build.sh hierarchy.Example/
cd build/bin
```

Now you should find different applications for the different components.
Start them all by calling `./run.sh` and have a look at what the `Sink` 
component does (call `tail -f hierarchy.Example.sink.log`).
It should give you the following output:
```
Using libmosquitto 1.6.10
Connected to MQTT broker
Connected to MQTT topic /state/hierarchy/Example/sink
Connected to MQTT topic /replayFinished/hierarchy/Example/sink
Connected to MQTT topic /connectors/hierarchy/Example/sink/value
Connected to MQTT topic /ports/hierarchy/Example/sink/value
Started.
Connected to MQTT topic /ports/hierarchy/Example/rs/value
Connected to MQTT topic /ports/hierarchy/Example/rs/value
Connected to MQTT topic /ports/hierarchy/Example/rs/value
Connected to MQTT topic /ports/hierarchy/Example/rs/value
Connected to MQTT topic /ports/hierarchy/Example/rs/value
Sink: 0
Sink: 1
Sink: 3
Sink: 6
Sink: 10
Sink: 15
Sink: 21
Sink: 28
Sink: 36
```

Let's simulate that the `Source` component fails using a second terminal window:
```
killall hierarchy.Source
```

In the first terminal window, you should now see that the `Sink` component 
doesn't produce anymore output because the `Source` component does not provide 
anymore input:
```
Sink: 45
Sink: 55
Sink: 66
Sink: 78
Sink: 91
Sink: 105
Sink: 120
```

Now restart the `Source` component using the second terminal window that you 
used to kill the `Source` component: 
```
./hierarchy.Source --name hierarchy.Example.source --brokerHostname localhost --brokerPort 1883 > hierarchy.Example.source.log 2>&1 &
```
If you now look at the terminal window that shows the output of the `Sink` 
component you can see that the output continues where it stopped before the 
failure.
