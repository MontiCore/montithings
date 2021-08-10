# Overview

The _SD4ComponentTestingGenerator_ generates a C++ test file out of one SD4ComponentTesting model. Actually the CoCos of SD4ComponentTesting work only on MontiArc models but in the future it is planned that one also can use MontiThings models with behavior. MontiThings offers a PrettyPrinter which transforms valid MontiThings models to MontiArc models. The PrettyPrinter can be found [here](https://git.rwth-aachen.de/monticore/montithings/core/-/blob/develop/languages/montithings/src/main/java/montithings/_visitor/MontiThingsToMontiArcFullPrettyPrinter.java). The generated C++ test works with the original MontiThings models and also with the MontiArc models. The main restriction is that MontiArc has no behavior.


# Usage

There are two possibilities to use the generator. The first way to generate the C++ test file is to use the _generate_ method of the _SD4ComponentTestingGenerator_ class:

`SD4ComponentTestingGenerator.generate(ast, path);`

This method generates the C++ test file for the given _ASTSD4Artifact ast_ of one SD4ComponentTesting model and save the file in the given _path_. To create the AST for the method, one can use the method _loadModel_ from the class _SD4ComponentTestingTool_. The method creates the needed symboltable, creates the AST and also checks all CoCos that the given AST is valid. For further information see [Tutorial usage of SD4ComponentTestingTool](). 

###### TODO auf den Abschnitt im Tutorial verlinken

The _generate_ method can only generate a test file for one single SD4ComponentTesting model. If one want to generate several test file one have to call the _generate_ method several times.

The second Method is to use the SD4ComponentTestingCLI via the CLI. Then the CLI calls the _generate_ method of the _SD4ComponentTestingGenerator_ class with the given values and creates the AST for the given models. For further information see [Tutorial usage of CLI]().

###### TODO verlinken auf den Abschnitt im Tutorial


# Procedure of the generator

The generated test has five parts:

1. [The initialization](#1-the-initialization)
2. [The struct](#2-the-struct)
3. [The observer](#3-the-observer)
4. [The initialization of the test method](#4-the-initialization-of-the-test-method)
5. [The asserts](#5-the-asserts)

For a better understanding of the procedure of the generator we discuss the procedure with the example [ProcedureExample.sd4c](https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting/-/blob/develop/src/test/resources/examples/correct/ProcedureExample.sd4c) Testdiagram. It has the mainComponent [Main](https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting/-/blob/develop/src/test/resources/examples/correct/Main.arc) which has two subcomponents of the same MontiArc component type [Sum](https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting/-/blob/develop/src/test/resources/examples/correct/Sum.arc). The Sum component type gets two values and should calculates the sum of them. Since MontiArc has no behavior we consider the component as a blackbox with the desired behavior.

## 1. The initialization

In the first part, the generator includes the needed header files. Beside some external libraries, the generator includes the header file of the given _mainComponent_ MontiArc component type and all header files of the subcomponent types of it. After the includes, the generator print some initialization of the external libraries. The  complete template can be found [here]().

For the example the generator would print the following:

```cpp
// initialization
#include "easyloggingpp/easylogging++.h"
#include "gtest/gtest.h"
#include <chrono>
#include <thread>

#include "Main.h"

#include "Sum.h"

INITIALIZE_EASYLOGGINGPP

```

## 2. The struct

The second step is the printing of the struct for the test. The template for the struct can be found [here]().

The struct has a variable for the _mainComponent_ and three variables for all subcomponents of the _mainComponent_. These variables are for the component, the implementation (impl) and the state of the subcomponent. They are needed to get access to the ports of the components and in the future also to get access to more functionalities of MontiThings components.

In the first part of the struct the generator prints the memory reservation of all needed variables. The second part of the struct is the constructor. The generator prints the creating of the object for the component type of the _mainComponent_ and the assignments of the variables of the subcomponents. The last part of the struct is the destructor which destruct the _mainComponent_ object and therefore all objects of the subcomponents.

The generator would print the following for the example:
```cpp
// struct for test
struct MainTest : testing::Test
{
  montithings::examples::correct::Main *cmpMain;

  montithings::examples::correct::Sum *sumComCmp;
  montithings::examples::correct::SumImpl *sumComImpl;
  montithings::examples::correct::SumState *sumComState;

  montithings::examples::correct::Sum *sumCompCmp;
  montithings::examples::correct::SumImpl *sumCompImpl;
  montithings::examples::correct::SumState *sumCompState;


  MainTest ()
  {
    cmpMain = new montithings::examples::correct::Main ("examples.correct.Main");

    sumComCmp = cmpMain->getSubcomp__SumCom();
    sumComImpl = sumComCmp->getImpl();
    sumComState = sumComCmp->getState();

    sumCompCmp = cmpMain->getSubcomp__SumComp();
    sumCompImpl = sumCompCmp->getImpl();
    sumCompState = sumCompCmp->getState();
  }

  ~MainTest ()
  {
    delete cmpMain;
  }
};
```

## 3. The observer

The observer are needed to check if the expected messages going through the ports during the computing of the main MontiThing component (_mainComponent_). The template for the printing of all observer can be found [here](https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting/-/blob/develop/src/main/resources/templates/Observer.ftl).

The generator prints first a template for the observer which is called PortSpy. Then for every port of the _mainComponent_ the generator prints a single PortSpy class which records all messages going through the port. In the next step the generator prints for every port of every subcomponent of the _mainComponent_ a own PortSpy class. It is necessary that they are all unique, so the name of the classes are for ports of the _mainComponent_ ```PortSpy_compTypeName_portName``` and for the subcomponents ```PortSpy_compTypeName_compName_portName```. This guarantees that every port has its own observer, and the messages recorded correctly.


## 4. The initialization of the test method

## 5. The asserts

# License

© https://github.com/MontiCore/monticore

For details on the MontiCore 3-Level License model, visit
https://github.com/MontiCore/monticore/blob/dev/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md

# Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [CD4Analysis Project](https://github.com/MontiCore/cd4analysis)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
