<!-- (c) https://github.com/MontiCore/monticore -->
# Initialization

In some situations, developers want to initialize their components.
For example, weight sensors may need to define a "zero level" when starting 
(called "tare weight").
For this, MontiThings provides the `init` construct. 
In can be used in two variants: with and without referencing ports. 
The following example of a `Loop` component showcases both of these variants.

<img src="../../../docs/Initialization.png" alt="drawing" width="200px"/>

The `Example` component specifies that the outgoing port `output` shall be 
connected to the incoming port `input` of the same `Loop` component.
This means the component sends messages to itself.
Therefore, each outgoing message will trigger the component to process a this 
outgoing message on its incoming port.

First the `Loop` component initializes itself by sending a message `1` on its 
`output` port.
The `init` block is executed right before the component starts. 
You can think of it as the equivalent of the `setup()` method in Arduino.
```
init {
  output = 0;
}
```

The `Loop` component also defines an initialization specific to its incoming 
port:
```
init input {
  log("First Input: " + input);
  after 1s {
    output = input + 1;
  }
}
```
This code is executed when the component receives the first message on this 
port.
It is useful, for example, to set store initial sensor values like the tare 
weight.


The normal behavior of the `Loop` component is the same as the `init input`, 
but omits the `First` in the log message.
Overall, the `Loop` component logs the value of incoming messages, waits one 
seconds and then sends the received value increased by one to itself. 
Effectively, this it behaves the same as a piece of (pseudo-)code with a loop:
```
int i = 0;                    // init 
log ("First Input: " + i);    // init input
while (true) {                // self-loop connector
  log ("Input: " + i);        // normal behavior
  i += 1;                       
}
```
Therefore, we end up with the following output:
```
DEBUG: 2021-10-20 23:36:49,138 Started.
INFO: 2021-10-20 23:36:49,140 First Input: 0
INFO: 2021-10-20 23:36:50,145 Input: 1
INFO: 2021-10-20 23:36:51,148 Input: 2
INFO: 2021-10-20 23:36:52,153 Input: 3
INFO: 2021-10-20 23:36:53,154 Input: 4
INFO: 2021-10-20 23:36:54,155 Input: 5
...
```
