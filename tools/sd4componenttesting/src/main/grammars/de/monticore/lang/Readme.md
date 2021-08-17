# Overview
As we have seen before, with our tool _SD4ComponentTestingTool_ we want to offer a new and easier way to describe tests for given models in an IoT scope. Therefore, it is necessary to extend an already existing grammar or to write a complete new one. Because of useability and, i.e., reusability we decided to extend an existing grammar.
In the following sections we will describe the structure and offered functionality of the extended grammar.

# Grammar
For a better understanding we will show the full grammar first and then, in a second step, will describe the components of our grammar individualy.
```
package de.monticore.lang;

grammar SD4ComponentTesting extends de.monticore.lang.SD4Development,
                                    MontiArc,
                                    de.monticore.SIUnitLiterals  {
    start SD4Artifact;

    SD4Artifact =
      ("package" packageDeclaration:MCQualifiedName& ";")?
      MCImportStatement*
      TestDiagram;


    TestDiagram implements Diagram =
      "testdiagram" Name "for" mainComponent:Name "{"
        SD4CElement*
      "}";


    interface SD4CElement;

    SD4CConnection implements SD4CElement =
      (source:PortAccess)? "->" target:(PortAccess || ",")* ":" value:(Literal || ",")* ";";

    SD4CExpression implements SD4CElement =
      key("assert") Expression ";";

    SD4CDelay implements SD4CElement =
      key("delay") SIUnitLiteral ";";
}
```

## Extension
To describe tests for given models as easy as possible we decided to use an extension of a sequence diagram.
That is why our _SD4ComponentTestingTool_ defines and uses a new grammar to offer an additional small set of functionality.
Because we want to extend sequence diagrams with our own functionality, we choose the [_de.monticore.lang.SD4Development_](https://git.rwth-aachen.de/monticore/statechart/sd-language) extension to extend which is also part of the _de.monticore.lang_ package. In addition, to offer alredy existing functionality we extend [_MontiArc_](https://git.rwth-aachen.de/monticore/montiarc/core) and [_de.monticore.SIUnitLiterals_](https://github.com/MontiCore/siunits) for further opportuinites for future asserts and assignements.

```
package de.monticore.lang;

grammar SD4ComponentTesting extends de.monticore.lang.SD4Development,
                                    MontiArc,
                                    de.monticore.SIUnitLiterals  {
```

## Start point
Our grammar starts with a new startpoint _SD4Artifact_ which is different to the start of _SD4Developement_ because of lots of errors and warnings in case of overriding. 
As we can see here, we offer an optional package name with the keyword `package` followed be the _packageDeclaration_ as _MCQualifiedName_ and `;`. As a second, different imports can be done as _MCImportStatement_. 
After all initializations, declarations and imports are done the actual test diagram is invoked with _TestDiagram_.

```
    start SD4Artifact;

    SD4Artifact =
      ("package" packageDeclaration:MCQualifiedName& ";")?
      MCImportStatement*
      TestDiagram;
```

## Interfaces
To make things a bit easier and, i.e., easier to extend for future work, we use different interfaces to fill our test diagram with content and further grammar. Therefore, we use one the one hand our own interface _SD4CElement_ and on the other hand the interface _Diagram_ of the extension [_BasisSymbols_ of _MCBasis_] (https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/symbols/BasicSymbols.mc4#L44).

### Diagram 
At first, let us have a look at the interface _Diagram_ which we implemnt with our _TestDiagram_:
``` TestDiagram implements Diagram =
      "testdiagram" Name "for" mainComponent:Name "{"
        SD4CElement*
      "}";
```
As we can see here, we start a testdiagram of our _SD4ComponentTestingTool_ with the keyword `testdiagram` followed by a selfchoosen name (_Name_), the keyword `for` and the _mainComponent_ as _Name_. In general, the _mainComponent_ will be the main model of the _MontiArc_ model which also describes and names the functions of the generated tests ([see generator] (https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting/-/tree/develop/src/main/java/de/monticore/lang/sd4componenttesting/generator)).
After initiating the testdiagram, the actual test conditions can be placed inside the brackets `{ }`.
To terminate the testdiagram we also use the terminate symbol `;`. 


### SD4CElement
As second interface we use _SD4CElement_ which implements all test conditions and test instructions, e.g., [OCL](https://git.rwth-aachen.de/monticore/languages/OCL).
```
    SD4CConnection implements SD4CElement =
      (source:PortAccess)? "->" target:(PortAccess || ",")* ":" value:(Literal || ",")* ";";

    SD4CExpression implements SD4CElement =
      key("assert") Expression ";";

    SD4CDelay implements SD4CElement =
      key("delay") SIUnitLiteral ";";
```
For a better overview we describe every implementation individual:

#### SD4CConnection
The _SD4CConnection_ describes the main part of the _SD4CElement_. This implementation denotes the connection between different components and subcomponents. For this, we use an optional _PortAccess_ as _source_ with the keyword `->` followed by a list of _PortAccess_ _targets_. After the keyword `:` a list of _values_ (_Literals_) must be appended and with `;` terminatd.
```
    SD4CConnection implements SD4CElement =
      (source:PortAccess)? "->" target:(PortAccess || ",")* ":" value:(Literal || ",")* ";";
```
Please note that it is possible either one target with exact one value or multiple targets with exact one value or multiple targets with multiple values if the number of targets and number of values is equal.
Further notice, that it is not inteded to only use a connection like `-> target:2;`.
Whenever the source is missing we assume value is the input of target which is input of the _mainComponent_.
On the other side, please notice that we always assume source is the output of the _mainComponent_ with value _value_, whenever _target_ is not present. 
In all other cases we assume _source_ and _target_ are normal ports of either the _mainComponent_ or a subcomponent.
To differentiate, we use port names like `subcomponent.port` and `mainComponentPort`. Please note here that the subcomponent is seperated with an `.` between subcomponent and port.

Some examples for MainInput and MainOutput of _mainComponent_ Test
```
 -> input : 12;
```
With this expression we assume the input port of the _mainComponent_ Test has the value 12.

```
output -> : 12;
```
With this expression we assume the output port of the _mainComponent_ Test has the value 12.

#### SD4CExpression
With the next implementation _SD4CExpression_ we offer the functionality of [OCL](https://git.rwth-aachen.de/monticore/languages/OCL).
After the keyword `assert` all _Expression_ can be inserted as soon as they are evaluateable to a boolean.
I.e., it is possible to check a range of a port.
```
    SD4CExpression implements SD4CElement =
      key("assert") Expression ";";
```
E.g., assume you want to check whether the value of the subcomponent _Sub_ port _subPort_ is smaller than 6 but greater than 2 we can write somethinh like:
```
    assert Sub.subPort >= 2 && Sub.subPort <= 6;
```


#### SD4CDelay
Our last implementation of the interface _SD4CElement_ is _SD4CDelay_. This can be used to request a delay between calculations inside the tests to emulate delay which IoT devices might have in a real application.
For this, we use the keyword `delay` followed by a _SIUnitLiteral_ and a `;`.
The _SIUnitLiteral_ contains SI units like _ms_, _s_, _m_ and also _h_.
```
    SD4CDelay implements SD4CElement =
      key("delay") SIUnitLiteral ";";
```
We assume we want to test the behavior of a component with a delay to test a bit closer to a real-world application:
```
    delay 500 ms;
```

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
