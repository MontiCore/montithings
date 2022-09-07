<!-- (c) https://github.com/MontiCore/monticore -->
# cd2proto
***
# Table of Contents
1. [Abstract](#Abstract)
2. [Introduction](#Introduction)
3. [Protobuf](#Protobuf)
4. [CD4A Language](#CD4A Language)
5. [Limitations](#Limitations)
   1. [Circular Associations](#Circular Associations)
6. [Design Decisions](#Design Decisions)
   1. [Templates](#Templates)
   2. [The Super Attribute](#The Super Attribute)
7. [Evaluation](#Evaluation)
8. [Conclusion](#Conclusion)
9. [Future Work](#Future Work)

## Introduction <a name="Introduction"></a>
This module contains our implementation of a generator that takes a Class Diagram (.cd) and builds a corresponding Protobuf file (.proto) from it.
The Protobuf file can then be compiled by a compiler that is provided by Google to generate a file in a target language of choice, which then contains all necessary methods to serialize data conform with the provided class diagram. 
However, due to limitations from Protobuf and time limitations not all features of the Class Diagram language can be supported or are not supported yet, mainly concerning associations.

In this small documentation we will present the ``cd2proto generator``, its limitations and discuss some of our design decisions.
For more detailed descriptions of the many different classes that make up this module we have also written a README.

The main goal of this work was to offer a way to serialize structured data, that supports at least C++ and Python while having a minimal amount of dependencies. 
What we ended up with is an independent, easy to use generator that takes a class diagram and generates a Protobuf file, which, when compiled, offers a variety of methods to serialize and deserialize data.
These methods can then be used in other projects such as MontiThings to share data cross languages.

In this document we give a short overview of the different technologies used as well as the generator itself, its limitations and design decisions.
Later on we discuss our Evaluation, for which we used MontiThings, with more detailed documentation [inside the corresponding module](https://git.rwth-aachen.de/se-student/ss22/lectures/sle/student-projects/protobuf/montithings/-/tree/protobuf-serialization/applications/face-id-door-opener), before we conclude
this document by discussing what we achieved and what Future Work is left to be done.

## Protobuf <a name="Protobuf"></a>
Google Protobuf (https://developers.google.com/protocol-buffers) is a set of tools to serialize data in a fast and efficient way.
It is an Interface Definition Language, developed by Google, that enables easy to use cross language compatability and offers a compact serialized format to use.
A ``.proto`` file mainly consists of ``messages``. Below is an example of such a ``message``.
```
1   message Person {
2     string name = 1;
3     int32 id = 2;
4     repeated string email = 3;
5   }
```
This ``message`` defines a ``Person`` and has three attributes ``name``, ``id`` and ``email``. These attributes are defined
by their corresponding ``fields``, e.g. line 2-4 are three separate fields, one for each attribute.
Each field has to have a field identifier, denoted by the equal sign and the following number.
It is used to [safely allow updates](https://developers.google.com/protocol-buffers/docs/proto3#updating) to the Protocol Buffer description.

Additionally, each field also supports an additional keyword such as ``repeated`` (line 4), which in this case simply denotes, that there might be multiple separate ``email`` values to be serialized.

Protobuf supports more features such as ``enums`` and ``package`` declarations, that we make use of.

## CD4Analysis Language <a name="CD4A Language"></a>
As models to generate from, we use the Class Diagram Language **CD4Analysis** (https://github.com/MontiCore/cd4analysis). An example can be found in the **Example** section further down.
The CD4Analysis (CD4A) language is mainly used to model data structures or UML class diagrams. 
From a top-level view, a class diagram, modeled in CD4A consists of ``classes``, ``attributes``, ``associations`` and ``enumerations``.
A ``class diagram`` mainly consists of ``classes``, which contain corresponding ``attribues`` and can be wrapped inside ``packages``.
These classes can additionally be associated with one another by using ``associations`` to model more elaborate data stuctures.

A detailed documentation and more complex examples can be found at: https://github.com/MontiCore/cd4analysis/blob/master/src/main/grammars/de/monticore/cd4analysis.md

We use this language mainly to derive the specified data structures and generate corresponding protocol buffers, which enable easy to use serializing and deserializing of the defined data structures.

## Proto Generator <a name="Proto Generator"></a>

The central piece of cd2proto is
the [ProtoGenerator class](src/main/java/montithings/generator/cd2proto/ProtoGenerator.java).
It looks for class diagram files (.cd) in the specified directories and generates the corresponding
Protocol Buffer description files (.proto).

### Example <a name="Example"></a>

Coorperation.cd

```
classdiagram Corporation {
    abstract class Person {
        String firstname;
        String lastname;
    }

    class Employee extends Person {
        int employeeId;
        long salary;
    }

    class Intern extends Person {
        boolean getsPaid;
    }

    association [0..1] Employee <- (reportsTo) Intern [*];
}
```

The class diagram above shows a simple class diagram that holds three classes:

* ``abstract Person``
* ``Employee``
* ``Intern``

With Employee and Intern being subclasses of Person, thus inheriting the ```firstname```
and ```lastname``` attributes.

Running the ProtoGenerator like this...

```java
// The directory to place the generated .proto files to
Path outDir = Paths.get("target/output_directory");
// The directory to look for class diagrams
Path modelPath = Paths.get("src/test/resources/classdiagrams");
// The fully qualified name of the model to translate
String modelName = "Corporation";

ProtoGenerator generator = new ProtoGenerator(outDir, modelPath, modelName);
Set<Path> protoFiles = generator.generate();
```

...yields a Protocol Buffer description of the model.

```protobuf
syntax = "proto3";
package montithings.Corporation.protobuf;

message Person {
    // Fields
    string firstname = 1;
    string lastname = 2;
}

message Employee {
    // Parent class
    Person super = 1;
    // Fields
    int32 employeeId = 2;
    int64 salary = 3;
}

message Intern {
    // Parent class
    Person super = 1;
    // Fields
    bool getsPaid = 2;
    // Associations
    repeated Employee employee = 3;
}
```

For further details on the translation of class diagrams to Protocol Buffer
descriptions [see here](doc/translation.md).

## Limitations <a name="Limitations"></a>
There is one large limitation that we encountered, which we would like to discuss in this section.

### Circular Associations <a name="Circular Associations"></a>

A circular association occurs on three different instances.
* A self-loop (e.g. ```association [0..1] Employee <- (reportsTo) Employee [*]; ```)
* A bidirectional association (e.g. ```association [0..1] Employee <-> (reportsTo) Intern [*]; ```)
* Multiple associations forming a loop (e.g.```association [0..1] Intern -> (reportsTo) Employee [*]; association [0..1] Employee -> (reportsTo) Boss [*]; association [0..1] Boss -> (reportsTo) Intern [*]; ```)

Loops on associations are not supported because of our design of the template.
[...]



To detect loops we use ```NoCircleCoCo```, a Context Condition, using the CoCo infrastructure provided by MontiCore.

When parsing a class diagram, a ```ASTCDCompilationUnit``` element is generated, which builds the root of the AST. 
This compilation unit element contains a definition (```ASTCDDefinition```), which in turn holds a list of all associations occurring inside the parsed class diagram.
Since every association also holds information about the classes that are associated, we can use this list to build a graph, using ```Graph.java``` and ```Vertex.java```, and run a simple algorithm, that computes, whether the proivded graph contains a cycle.

## Design Decisions <a name="Design Decisions"></a>
While implementing the ``cd2proto`` generator many decisions had to be made.
In this section we present the two most impactful ones, which were how we dealt with inheritance, since it is not natively supported by Protobuf, and whether we want to use Templates or a Pretty Printer to generate code.

### The "Super" Attribute <a name="The Super Attribute"></a>
There is a question that occurs when looking at inheritance of classes. Looking back at our Example, ```Cooperation.cd```, there are two classes,
```Employee``` and ```Intern```, that are subclasses of the ```Person``` class.

Since Protobuf natively does not support inheritance, the question that rises up is, how do we carry the information about the parent class.
There were two approaches that naturally made sense to us.

**Approach 1**
Our first approach entailed simply copying all attributes that the parent class provides into the subclass.

**Approach 2**
The second approach was to just have a single additional attribute ```super``` that has the parent class as its type.

Since we have to carry the information either way, the approaches are almost equivalent in terms of data that has to be serialized and transmitted. However, the second approach
has the additional advantage of easier readability, both in the resulting ```.proto``` file and in the generated code. The files are potentially a lot shorter in terms of amount of lines of code and the parent class/subclass relation is a lot more visible.<br />
In the resulting code the transmitted objects are of different classes, but if a user wants to recover the original
class relations, the second approach also makes that a lot easier.

### Templates

The choice that presented itself to us, was whether we wanted to use __Templates__ or a __Pretty Printer__ for the generation of the ``.proto``.

**Pretty Printer**
A Pretty Printer is a tool to print out an AST in a more readable form or even for our case could be used to generate usable code.

**Templates** 
Templates are used as a blueprint to generate code from the AST. In MontiCore FreeMarker is used. For this purpose a GeneratorEngine exists, which lets us easily create a generator, that takes a FreeMarker template (`.ftl`)
and an AST node of the model and then iterates over the AST producing output corresponding to the contents of the template.

For the ``cd2proto`` generator we chose to use MontiCore's GeneratorEngine over implementing a Pretty Printer.
The main reason for that is the reduced amount of complexity and increased modularity templates offer, since for a Pretty Printer more code would have to be written.
We think there is a discussion to be had still, since using a Pretty Printer would reduce overall dependencies.
But to implement such a Pretty Printer, we would either have to make big transformations to the AST or simply implement the Protobuf grammar
using MontiCore to generate the AST infrastructure, which we could then use to translate the existing class diagram AST into.


## Evaluation <a name="Evaluation"></a>

The initial goal of the pitch was to eventually use this generator to serialize data that is shared between MontiThings components with cross-language support. In this section we give a short overview on MontiThings and how we evaluated the generator using it.
More detailed information can be found in the MontiThings repository and our other project.

### MontiThings <a name="MontiThings"></a>
MontiThings is a DSL for designing Internet of Things architectures. A model consists of several elements:
* components - The building block of any architecture
* port - A place where information can be transmitted
* connector - Connection between ports of components

In short, a model consists of ``components``, these ``components`` have ``ports``, with ``connectors`` connecting the ports and thus the different components.
Additionally, the behaviour of a component can be modeled inside the language.
Sometimes, this native behaviour is not enough and one might want to have __handwritten code__ defined behaviour.
This is the most relevant part for our evaluation, since the overall goal was to enable different components to share protobuf serialized data with cross-language support.
Since MontiThings natively translates MontiThings behaviour into C++ code, a component running e.g. Python code, is always running handwritten code.

To evaluate the generator we generated from the following model:
```
package unlock;

classdiagram FaceUnlock {
    package Person {
        class Person {
            public String name;
            public boolean allowed;
            public int visitor_id;
        }
    }
    package Image {
        class Image {
            int person_id;
        }
    }
}
```

And used the resulting ```.proto``` to serialize data, inside an architecture, that was running both MontiThings behaviour (C++) and __handwritten__ Python code.
More details on this can be found in the corresponding project. 

## Conclusion
In this document we presented our work of the last few months and gave a short introduction to the 
technologies used and presented the main work of this module, the `cd2proto` generator. We would like to emphasize here again, that this module, although being evaluated
in MontiThings, is independent of it and can be used in any project that wants to use protocol buffers generated from 
class diagrams.

In short, this generator takes a class diagram file (`.cd`) and generates a Protobuf file (`.proto`) from it.
From this `.proto` file, methods to serialize and deserialize data within the defined data structures can be generated in a target language of choice using the corresponding Protobuf compiler.
The main advantage for our purposes was that we can generate into different languages, and enable cross language support for applications.

## Future Work

### Component Behaviour Written in the Go language

This repository [has a branch](https://git.rwth-aachen.de/se-student/ss22/lectures/sle/student-projects/protobuf/montithings/-/blob/lang/go/READMEGO.MD) which aims to add support for handwritten behaviour files written in the [Go Programming Laguage](https://go.dev/).
Unfortunately the implementation is incomplete as of now and needs further work.
The linked README describes the current state and is needed to be done.

As mentioned in the **Limitations** section, associations that form a cycle are not supported. We think that this can be achieved though. As described earlier, using a `uuid` system, we think that associated elements could be further tracked
beyond the first full cycle, but it has not been implemented yet. Aside from that further testing with a larger variety of different test cases is needed.
Although in theory a lot of languages are already supported, actual testing so far has only been done with C++ and Python and should be done with a larger variety of languages.

### Adding support for more types

If the CD4A language is extended with more types or support for currently unsupported types is required, adding support for
extra types is very straightforward.

The way cd2proto resolves a type from the class diagram to a java type is with the help of the `TypeHelper` (helper/TypeHelper.java).
The TypeHelper supports four types of `SymTypeExpression`'s from MontiCore, each resolved by their own class implementing the `ITypeResolver`
interface (found in `helper/resolver/`).

* SymTypeConstant for primitive types (`PrimitiveTypeResolver`, int, long, ...)
* SymTypeArray for arrays (`ArrayTypeResolver`, Currently, only one-dimensional arrays are supported)
* SymTypeOfGenerics for generic types (`GenericTypeResolver`, List<T>, Map<T, E>, ...)
* SymTypeOfObject for types other types registered in the scope of the CD (`ObjectTypeResolver`)

To add support for a new type extend the implementation of the corresponding type resolver. If support for a new kind of type is required,
also extend the implementation of `TypeHelper` by creating a new `ITypeResolver` class that handles the new types and adding it to the `typeMap`
in the constructor.

## Authors and acknowledgment

* André Fugmann
* Merlin Freiherr von Rössing
* Sebastian Grabowski
* Tim Nebel
