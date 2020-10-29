# MontiThings Core Project

© https://github.com/MontiCore/monticore; Contact: @christian.kirchhof

The MontiThings Core repository contains everything related to the common basis of the MontiThings architecture description, 
a [MontiArc][montiarc]-based architecture description language for rapid prototyping of Internet of Things applications.

<img src="docs/MontiThingsOverview.png" alt="drawing" height="400px"/>

In MontiArc, architectures are described as component and connector systems in which autonomously acting components perform 
computations. Communication between components is regulated by connectors between the components’ interfaces, which are stable 
and build up by typed, directed ports. Components are either atomic or composed of connected subcomponents. Atomic components 
yield behavior descriptions in the form of embedded time-synchronous port automata, or via integration of handcrafted code. 
For composed components the behavior emerges from the behavior of their subcomponents. 

While MontiArc generates code for simulations, MontiThings generates code to be executed on real devices.

# Prerequisites 
- Git (for checking out the project)
- Maven (for building the project); Gradle is still under development, use Maven!
- Java 8 or 11 or 14 (other versions are not checked by the CI pipeline)
- [NNG (for networking)][nng] (Please use [version 1.3.0][nng-1.3])
- GCC and CMake (For compiling the generated C++ code)

# Getting Started

```
git clone git@git.rwth-aachen.de:monticore/montithings/core.git
cd core
mvn clean install
```

Now the project should start building. This can take a while (10-15 minutes are normal). 

Once the project is built, you can look at the generated source code. The `application` folder contains some example applications. Each of them should now contain a `target/generated-sources` subdirectory. If you want, you can reformat the generated sources for better readability using the `reformatCode.sh` script (requires clang-format). Within that directory you can find the generated source. Within one of these folders, you can compile them by running 
```
mkdir build; cd build
cmake -G Ninja ..; ninja
```
You should then be able to find the binaries in the `bin` folder. 

# License

© https://github.com/MontiCore/monticore

This repository is currently non-public. 

[se-rwth]: http://www.se-rwth.de
[montiarc]: https://git.rwth-aachen.de/monticore/montiarc/core
[nng]: https://github.com/nanomsg/nng#quick-start 
[nng-1.3]: https://github.com/nanomsg/nng/tree/v1.3.0