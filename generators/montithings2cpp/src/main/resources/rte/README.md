<!-- (c) https://github.com/MontiCore/monticore -->
# MontiThings Runtime Environment (RTE)

The runtime environment (RTE) contains code that the generated code is based on.
Whenever the generator generates code, the RTE is copied to the target 
directory.
Here's an overview of the most important classes of the RTE:

<img src="../../../../../../docs/RteCDv2.png" alt="drawing" height="400px"/>

The `UniqueElement` just provides a UUID (`sole::uuid4`) to each class that 
extends it. 
This UUID is mostly used for differentiating elements that read messages from 
ports.
Consequently, each `Port` is also a `UniqueElement`. 

## Ports

`Port`s are the basic element for receiving and sending messages between 
components. 
If ports need to send data between different devices (i.e. over a network) or 
applications (e.g. via inter-process communication) more technical 
implementations of the `Port` can be used. 
Currently, MontiThings provides `WSPort` (for communication via websockets) and 
`IPCPort` (for inter-process communication).
Moreover, there are two more structures based on `Port`: `MultiPort` and 
`InOutPort`. 
A `MultiPort` groups multiple ports. 
This is useful if port needs to communicate using multiple communcation 
technologies (e.g. if one receiver requires a `WSPort` and another receiver 
requires a `IPCPort`).
A `InOutPort` is the port that is actually instantiated by the components to 
implement the ports of the architecture. 
The `InOutPort` consists of two ports (an incoming and an outgoing port) that 
conceptually act as one port. 
This is useful if the communication technology used for input does not match 
the communication technology used for output.

## Components
`IComponent` is the base class for all generated components. 
It defines the (virtual) methods that the generator needs to implement for each
component. 
Similarly, `IComputable` provides the (virtual) methods that need to be 
implemented by the hand-written code for components whenever an `Impl` class
is provided for a component.
The `ManagementCommunication` class is used for exchanging management messages
with other components (e.g. at which IP address another component can be 
reached).




