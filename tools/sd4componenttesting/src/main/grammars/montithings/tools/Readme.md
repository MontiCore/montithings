<!-- (c) https://github.com/MontiCore/monticore -->
# Overview
As we have seen before, with our tool _SD4ComponentTestingTool_ we want to offer a new and easier way to describe tests for given models in an IoT scope. Therefore, it is necessary to extend an already existing grammar or to write a complete new one. Because of usability and, i.e., reusability we decided to extend an existing grammar.
In the following sections we will describe the structure and offered functionality of the extended grammar.

# Grammar
For a better understanding we will show the full grammar first and then, in a second step, we will describe the components of our grammar individually. For this, we classify this section in extension, start point and the used interfaces.
```
package montithings.tools;

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
Because we want to extend sequence diagrams with our own functionality, we choose the [de.monticore.lang.SD4Development](https://git.rwth-aachen.de/monticore/statechart/sd-language) extension to extend which is also part of the _de.monticore.lang_ package. In addition, to offer already existing functionality we extend [MontiArc](https://git.rwth-aachen.de/monticore/montiarc/core) and [_de.monticore.SIUnitLiterals_](https://github.com/MontiCore/siunits) for further opportunities for future asserts and assignments.

```
package montithings.tools;

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
To make things a bit easier and, i.e., easier to extend for future work, we use different interfaces to fill our test diagram with content and further grammar. Therefore, we use on the one hand our own interface _SD4CElement_ and on the other hand the interface _Diagram_ of the extension [BasisSymbols of MCBasis](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/symbols/BasicSymbols.mc4#L44).

### Diagram 
At first, let us have a look at the interface _Diagram_ which we implement with our _TestDiagram_:
``` TestDiagram implements Diagram =
      "testdiagram" Name "for" mainComponent:Name "{"
        SD4CElement*
      "}";
```
As we can see here, we start a testdiagram of our _SD4ComponentTestingTool_ with the keyword `testdiagram` followed by a selfchoosen name (_Name_), the keyword `for` and the _mainComponent_ as _Name_. In general, the _mainComponent_ will be the main model of the _MontiArc_ model which also describes and names the functions of the generated tests ([see generator](https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting/-/tree/develop/src/main/java/de/monticore/lang/sd4componenttesting/generator)).
After initiating the testdiagram, the actual test conditions can be placed inside the brackets `{ }`.


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
The _SD4CConnection_ describes the main part of the _SD4CElement_. This implementation denotes the connection between different components and subcomponents. For this, we use an optional _PortAccess_ as _source_ with the keyword `->` followed by a list of _PortAccess_ _targets_. After the keyword `:` a list of _values_ (_Literals_) must be appended and with `;` terminated.
```
    SD4CConnection implements SD4CElement =
      (source:PortAccess)? "->" target:(PortAccess || ",")* ":" value:(Literal || ",")* ";";
```
Please note, that not all combinations of targets and values are allowed and reasonable.
For example, if the target is not present, there can be only exact one value.
In addition, there are two other cases: if multiple targets are present they can either have exact one value or multiple values as well.
But in this case, the number of targets must be equal to the number of values at all time.

Further notice, that it is not intended to only use a connection like `-> target : 2;`.
Whenever the source is missing we assume the value is the input of target which is input of the _mainComponent_.
On the other side, please notice that we always assume source is the output of the _mainComponent_ with value _value_, whenever _target_ is not present. 
In all other cases we assume _source_ and _target_ are normal ports of either the _mainComponent_ or a subcomponent.
To differentiate, we use port names like `subcomponent.port` and `mainComponentPort`. Please note here that the subcomponent is separated with an `.` between subcomponent and port.

To clarify the different a bit more, here some short examples for MainInput and MainOutput of _mainComponent_ Test:

**Valid examples**
```
 -> input : 12;
```
With this expression we assume the input port of the _mainComponent_ Test has the value 12.
Please note, that with this expression a new calculation of the component starts.

```
output -> : 12;
```
With this expression we assume the output port of the _mainComponent_ Test has the value 12.
Please note, that with this expression only the very last value of the calculation is considered.

```
input -> subcomponent.input : 12;
```
With this expression we assume the input port of the _mainComponent_ is connected with the input port _input_ of the subcomponent _subcomponent_ and the _mainComponent_ input transfers its value to the _subcomponent.input_. Therefore, we test whether _subcomponent.input_ has the value _12_.

**Some invalid examples**
```
 -> subcomponent.input : 12;
```
`subcomponent.input` is no port of the _mainComponent_. Therefore, it is not valid.

```
subcomponent.input -> : 12;
```
`subcomponent.input` is no port of the _mainComponent_. Therefore, it is not valid.

```
 -> : 12;
```
There must be at least one port of the _mainComponent_. Therefore, it is not valid.

For better reference a more [practical example](#example) can be found in a following section. 

#### SD4CExpression
With the next implementation _SD4CExpression_ we offer the functionality of [OCL](https://git.rwth-aachen.de/monticore/languages/OCL).
After the keyword `assert` all _Expression_ can be inserted as soon as they are evaluable to a boolean.
I.e., it is possible to check a range of a port.
```
    SD4CExpression implements SD4CElement =
      key("assert") Expression ";";
```
E.g., assume you want to check whether the value of the subcomponent _sub_ port _subPort_ is smaller than _6_ but greater than _2_ we can write something like:
```
    assert sub.subPort >= 2 && sub.subPort <= 6;
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

# Example 
Sometimes an example can explain things better. Therefore, we will have a closer look at the [SmallExample](https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting/-/blob/LucasDevelopReadmeGrammar/src/test/resources/examples/correct/SmallExample.sd4c).
For this, we also need the _MontiArc_ models of the _mainComponent_ _Main_ and its subcomponent _Sum_.

**_MontiArc_ model of _Sum_**
```
package examples.correct;

component Sum {
  port in int first;
  port in int second;

  port out int result;
}
```
For the component _Sum_ three ports are defined: two input ports `first` and `second` and one output port `result`. All ports use and expect integer as datatype. Please notice, naming a port as _"result"_ may leed to some weird behavior and issues. However, using _result_ as port name for subcomponents seems to be fine to this point in time.


**_MontiArc_ model of _Main_**
```
package examples.correct;

component Main {
  port in int value;

  port out int foo;
  port out int foo2;

  Sum sumCom;
  Sum sumComp;

  value -> sumCom.first;
  value -> sumCom.second;
  sumCom.result -> foo;

  value -> sumComp.first;
  value -> sumComp.second;
  sumComp.result -> foo2;
}
```
As we can see here the _mainComponent_ _Main_ has three ports: `value` as input, `foo` and `foo2` as output. As in the component _Sum_ all ports expect an integer as datatype too.
Furthermore, _Main_ uses the component _Sum_ as subcomponent with variable _sumCom_ and _sumComp_.
The next lines describe the connections of the _mainComponent_ _Main_ with the subcomponent _sumCom_ and _sumComp_. I.e., the main input is connected with the inputs of the subcomponent _sumCom_ `first` and `second`. The output of the subcomponent `result` is connected with the output of the _mainComponent_ `foo`. The same holds for the subcomponent _sumComp_.

In a next step, we will have a look at the testdiagram model:

**_MontiArc_ model _SmallExample_**
```
package examples.correct;

testdiagram SmallExample for Main {
  -> value : 33;
  value -> sumCom.first : 33;
  value -> sumCom.second : 33;
  foo -> : 66;
  foo -> : 66;

  delay 500ms;

  assert sumCom.result < 67 && sumCom.result > 65;
}
```
At first we can define a package name, as we have seen before, as _MCQualifiedName_. Here, we use `examples.correct` as our package which is also defined in both other _MontiArc_ models _Main_ and _Sum_. 
In the next line we define a new testdiagram and call it `SmallExample` with `testdiagram SmallExample`. The appended `for Main` describes on which component this testdiagram _SmallExample_ should be rely. In this case, our testdiagram uses the _mainComponent_ _Main_ as basis for the tests. 
With this, we have constructed the basic structure of a testdiagram. All we have to do next is to describe the test cases and the expected values or further conditions like _OCL_ (as described above).
Taking a peak into the curly brackets we see a hand full of instructions. Some, about the same structure and some roughly different.
To make this as easy as possible, we will have a look at each instructions individual.

The first instruction ` -> value : 33;` describes the first input. As we have seen before, with this instruction we expect that the value _33_ lies against the port _value_. Because there is no source specified we have to assume the port _value_ must be an input port of the _mainComponent_ _Main_. If we take a look at the _MontiArc_ _Main_ model `[...] component Main {  port in int value; [...] }` we see that _Main_ has the input port _value_. Therefore, with this instruction, we can expect that the input port _value_ of the _mainComponent_ _Main_ has the value _33_.

The next two instructions are almost equal: `value -> sumCom.first : 33;`.
With this, we expect the port _value_ to give the port _first_ of the component _sumCom_ the value of _33_.
Let have us a close look to this instruction. Because of the previous instruction we know that the _mainComponent_ _Main_ has the port _value_. Because the source port `value` has no addition seperated with a `.` we have to assume that this port is a port of the _mainComponent_. With this we also know that _value_ is an input port. But `sumCom.first` is a black box to this point in time. 
If we look at the definition of the _MontiArc_ model of _Main_ 

**_MontiArc_ model of _Main_**
```
[...]
component Main {  
  [...]
  Sum sumCom; 
  [...]
  value -> sumCom.first;
  value -> sumCom.second;
  sumCom.result -> foo;
}
```
we see that the component _Main_ has a subcomponent _Sum_ which is named _sumCom_. Because of the next instructions `value -> sumCom.first;` and `value -> sumCom.second;` we know that the input port _value_ of _Main_ is connected with the ports _first_ and _second_ of the subcomponent _sumCom_ of type _Sum_. With the definition of the _MontiArc_ model of _Sum_ `[...] component Sum {  port in int first;  port in int second; [...] }` we know the ports _first_ and _second_ are input ports.
Therefore, we know that the input port _value_ of _Main_ is connected to the input port _first_ and _second_ of its subcomponent _sumCom_ of the type _Sum_. 
With this, the expectation the value of _value_ is transferred to the input ports _first_ and _second_ seams to be legit. So, if _value_ has the value _33_ we can expect _first_ and _second_ to have the same value _33_.

The next different instruction `foo -> : 66;` describes the end of a calculation. With this instruction we want to test whether the port foo has the value _66_. Please note, this instruction always tests the very last entry of the calculation.
As we have seen before `foo` has no additional content. Therefore this port must be a port of the _mainComponent_. The missing target port leads to the instruction of the main output. That is why we have to assume the main output port _foo_ of the _mainComponent_ _Main_ has the value _66_ as very last value. 
Through the _MontiArc_ models of _Sum_ and _Main_ we know that the output port _result_ of _Sum_ is connected to the output port _foo_ of _Main_. 
All values the port _result_ has must be transferred to the port _foo_ of _Main_. Therefore, the main output _foo_ should have the value _66_ if the initiated value _33_ has been doubled.
Please note, the instruction `sumCom.result -> foo : 66;` and `foo -> : 66;` have the same meaning.

Sometimes it must be necessary to wait a certain amount of time to emulate calculation time or transfer time of the real world application. That is why we use the next instruction `delay 500ms;`. With this, the calculation yields for about 500 ms and continues after the 500 ms as normal as before.

The very last instruction starts with the keyword `assert`. With this keyword expressions are enabled and _OCL_ can be used too.
In this case we only want to test whether the last result is in between a certain range of numbers.
If we take a look at the expression `assert sumCom.result < 67 && sumCom.result > 65;` we see the value of the port _result_ (output port of the subcomponent _sumCom_ of type _Sum_) should be less than _67_ but also greater than _65_. In case of calculating the double of the value _33_, _66_ should be in between _65_ and _67_.





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
