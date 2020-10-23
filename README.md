# MontiThings Core Project

The MontiThings Core repository contains everything related to the common basis of the MontiThings architecture description, 
a [MontiArc][montiarc]-based architecture description language for rapid prototyping of Internet of Things applications.

<img src="docs/MontiThingsOverview.png" alt="drawing" height="400px"/>

In MontiArc, architectures are described as component and connector systems in which autonomously acting components perform 
computations. Communication between components is regulated by connectors between the components’ interfaces, which are stable 
and build up by typed, directed ports. Components are either atomic or composed of connected subcomponents. Atomic components 
yield behavior descriptions in the form of embedded time-synchronous port automata, or via integration of handcrafted code. 
For composed components the behavior emerges from the behavior of their subcomponents. 

While MontiArc generates code for simulations, MontiThings generates code to be executed on real devices.

Contact: @christian.kirchhof

[se-rwth]: http://www.se-rwth.de
[montiarc]: https://git.rwth-aachen.de/monticore/montiarc/core

# Copyright

© https://github.com/MontiCore/monticore