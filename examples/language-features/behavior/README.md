<!-- (c) https://github.com/MontiCore/monticore -->
# Hierarchy

<img src="../../../docs/BehaviorExample.png" alt="drawing" width="600px"/>

This example demonstrates how to the define behavior of atomic components in 
MontiThings. In constrast to composed components, which define their behavior
by connecting subcomponents, atomic components do not contain subcomponents.
Instead, atomic components use either a Java-like statement language provided by 
MontiCore or plain C++ to describe their behavior. 

Behavior defined using the statement language can be added directly wihtin the 
model in the behavior block. This is most useful for "simple" behavior that does
not require much logic. 

<img src="../../../docs/BehaviorLpf.png" alt="drawing" width="600px"/>

More complex behavior can be implemented using implementation classes. For each
atomic component, a behavior class called like the component + `Impl` can be 
provided by the user. For example, for the `Sink` component, the according impl
class is called `SinkImpl`. The impl classes extend a class of the same name + 
`TOP`. For example, the `SinkImpl` class extends `SinkImplTOP`. You can read 
more about the TOP mechanism in the MontiCore reference manual. The 
`SinkImplTOP` requires the developer to implement two methods: 
`getInitialValues()` and `compute()`. `getInitialValues()` is called during the 
first compute cycle and should provide the components initial inputs. 
`compute()` is used to define the behavior of the component. For the `Sink` 
component it just writes the current input value to the standard output.

<img src="../../../docs/BehaviorSink.png" alt="drawing" width="600px"/>
