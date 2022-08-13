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
   2. [MQTT Implementation](#MQTT Implementation)
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

## Protobuf <a name="Protobuf"></a>

## CD4A Language <a name="CD4A Language"></a>

## Proto Generator <a name="Proto Generator"></a>

## Installation <a name="Installation"></a>

Kurzer Test wie denn installiert wird.
Was für Packages werden benötigt, etc.

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
* abstract Person
* Employee
* Intern

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



### MQTT Implementation <a name="MQTT Implementation"></a>

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
