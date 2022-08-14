# cd2proto
***
# Table of Contents
1. [Abstract](#Abstract)
2. [Introduction](#Introduction)
3. [Protobuf](#Protobuf)
4. [CD4A Language](#CD4A Language)
5. [Installation](#Installation)
   1. [Example](#Example)
6. [Limitations](#Limitations)
   1. [Circular Associations](#Circular Associations)
7. [Design Decisions](#Design Decisions)
   1. [Templates](#Templates)
   2. [The Super Attribute](#The Super Attribute)
8. [Evaluation](#Evaluation)
9. [Conclusion](#Conclusion)
10. [Future Work](#Future Work)

## Introduction <a name="Introduction"></a>
This module contains our implementation of a generator that takes a Class Diagram (.cd) and builds a corresponding Protobuf file (.proto) from it.
The Protobuf file can then be compiled by a compiler that is provided by Google to generate a file in a target language of choice, which then contains all necessary methods to serialize data conform with the provided class diagram. 
However, due to limitations from Protobuf and time limitations not all features of the Class Diagram language can be supported or are not supported yet, mainly concerning associations.

In this small documentation we will present the ``cd2proto generator``, its limitations and discuss some of our design decisions.
For more detailed descriptions of the many different classes we have also written a README, that can be found inside the top directory.

## Protobuf <a name="Protobuf"></a>
Google Protobuf (https://developers.google.com/protocol-buffers) is a mechanism to serialize data in a fast and efficient way.
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
Each field has to have a field identifier, denoted by the equal sign and the following number. [?]

Additionally, each field also supports an additional keyword such as ``repeated`` (line 4), which in this case simply denotes, that there might be multiple separate ``email`` values to be serialized.

Protobuf supports more features such as ``enums`` and ``package`` declarations, that we make use of.

## CD4Analysis Language <a name="CD4A Language"></a>
As models to generate from, we use the Class Diagram Language **CD4Analysis** (https://github.com/MontiCore/cd4analysis). An example can be found in the **Example** section further down.
The CD4Analysis (CD4A) language is mainly used to model data structures or UML class diagrams. 
From a top-level view, a class diagram, modeled in CD4A consists of ``classes``, ``attributes``, ``associations`` and ``enumerations``.
A ``class diagram`` mainly consists of ``classes``, which contain corresponding ``attribues`` and can be wrapped inside ``packages``.
These classes can additionally be associated with one another by using ``associations`` to model more elaborate data stuctures.

A detailed documentation and more complex examples can be found at: https://github.com/MontiCore/cd4analysis/blob/master/src/main/grammars/de/monticore/cd4analysis.md

We use this language mainly to derive the specified data structures and generate corresponding protocol buffers, which enable easy to use serializing and deserializing of the defined
data structures.

## Proto Generator <a name="Proto Generator"></a>

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

With Employee and Intern being subclasses of Person, thus inheriting the ```firstname``` and ```lastname``` attributes.


## Limitations <a name="Limitations"></a>

There were two major limitations that we encountered during implementation, which we go over in detail in this section.

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

### The "Super" Attribute <a name="The Super Attribute"></a>
There is a question that occurs when looking at inheritance of classes. Looking back at our Example, ```Cooperation.cd```, there are two classes,
```Employee``` and ```Intern```, that are subclasses of the ```Person``` class.

Since Protobuf natively does not support inheritance, the question that rises up is, how do we carry the information about the parent class.
There were two approaches that naturally made sense to us.

**Approach 1:** <br />
Our first approach entailed simply copying all attributes that the parent class provides into the subclass.
This has several advantages/disadvantages [...]

**Approach 2:** <br />
The second approach was to just have a single additional attribute ```super``` that has the parent class as its type.

Since we have to carry the information either way, the approaches are almost equivalent in terms of data that has to be serialized and transmitted. However, the second approach
has the additional advantage of easier readability, both in the resulting ```.proto``` file and in the generated code. The files are potentially a lot shorter in terms of amount of lines of code and the parent class/subclass relation is a lot more visible.<br />
In the resulting code the transmitted objects are of different classes, but if a user wants to restore the original
class relations, the second approach also makes that a lot easier.


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
As mentioned in the **Limitations** section, cyclic 