<!-- (c) https://github.com/MontiCore/monticore -->
# Basic Input Output

This example shows how to connect components to each other. 
The `Example` component contains two subcomponents. The `Source` component produces
values, the `Sink` component consumes these values and displays them on the 
terminal.

<img src="../../docs/BasicInputOutput.png" alt="drawing" width="500px"/>

MontiThings consists of a graphical notation and a textual notation. The 
graphical notation exists solely for easier comprehensibility. Only the textual
syntax can be processed by MontiThings. Here's the same `Example` component in 
textual syntax:

<img src="../../docs/BasicInputOutputExampleCode.png" alt="drawing" width="500px"/>

As you can see the `Example` component is marked with the `application` keyword.
If the system is compiled to a single binary, the `application` component will
be the entry point of the application (like a `main` function in C++ or Java).

After instantiating the two subcomponents `Source` and `Sink`, their ports are
connected using the `->` notation. The names of the ports are defined in the 
models of the `Source` and `Sink` components.

In contrast to the `Example` component, `Source` and `Sink` and sink are atomic
components. Atomic components do not have any subcomponents but instead define
their behavior using code written in a Java-like language or in C++ classes that
override the generated (empty) methods. More on this can be found in the 
behavior example. 

<img src="../../docs/BasicInputOutputSourceCode.png" alt="drawing" width="500px"/>

