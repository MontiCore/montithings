<!-- (c) https://github.com/MontiCore/monticore -->
# Context Conditions

In this Project several context conditions are used to apply context-sensitive restrictions on the SD4ComponentTesting grammar. For more Information about the overall structure of the grammar itself please read the [Grammar](../../../../../../grammars/de/monticore/lang/Readme.md) documentation.

The following is a general overview of all context conditions in this project. Each with a link that is pointing to a full description and an example of the context condition from the more detailed section bellow. Each of the following properties must be fulfilled, otherwise a testdiagramm is not valid and should not be used to use the generator to generate C++ test files.

- The testdiagram's main component requires the corresponding MontiArc model ([MainComponentExists](#maincomponentexists))
- A Connection requires:
    - The named Component and it's ports ([PortAccessValid](#portaccessvalid))
    - The Connection's definition in the Component ([SD4CConnectionConnectorValid](#sd4cconnectionconnectorvalid))
- Connections are plausible:
    - Source or target are not empty ([SD4CConnectionValid](#sd4cconnectionvalid))
    - Source named and target empty: source must be Output of Main Component ([SD4CConnectionMainOutputValid](#sd4cconnectionmainoutputvalid))
    - Source empty and target named: all targets must be Input of Main Component ([SD4CConnectionMainInputValid](#sd4cconnectionmaininputvalid))
    - Source and target named: Source must be Input of Main Component or Output of a SubComponent and target must be Output of Main Component or Input of SubComponent ([SD4CConnectionPortsDirectionValid](#sd4cconnectionportsdirectionvalid))
- Ensure that the OCL Expressions used are supported by the generator
  - Assignment and variable manipulation is not supported ([CurrentlyNotSupported](#currentlynotsupported))
  - Identifier must be resolvable ([NameExpressionsAreResolvable](#nameexpressionsareresolvable))
  - Restrict various OCL Expression constructs that are not supported ([OCLExpressionsValid](#oclexpressionsvalid))
  - Field access must be names and they must be resolvable ([FieldAccessExpressionsAreResolvable](#fieldaccessexpressionsareresolvable))
- Delays are plausible:
  - If a delay is specified, the time must not be 0 ([SD4CDelayGTZero](#sd4cdelaygtzero))
  - A delay always has to be specified in a time unit ([SD4CDelayValidUnit](#sd4cdelayvalidunit))

## Models used in examples

For the following examples we use these models for reference, which would be found in our model path.

```
package example;

component Main {
  port in int inPort;
  port out int outPort;

  Sum sumCom;

  inPort -> sumCom.first; 
  inPort -> sumCom.second;
  sumCom.outPort -> outPort;
}
```

```
package example;

component Sum {
  port in int first;
  port in int second;

  port out int outPort;
}
```

Although the SD4C testdiagram always is called MainTest, it is not the same model nor is the test case the same. Please also note, that the examples only serve to demonstrate the restriction of the grammar by the CoCos. You can not assume that even the examples marked as correct actually make sense for generating C++ tests. It is not part of the examples to distinguish between a wrong and correct test case.

## MainComponentExists

The SD4C grammar defines the structure of our test [Diagram](../../../../../../grammars/de/monticore/lang/Readme.md#diagram). Part of the testdiagram header is the main component, which will be specified with a model name. This CoCo checks if the main component is resolvable. If there is no model found for the main component, it will mark this check as a failure.

In the following example we use the nonxisting Foo as main component name, which will result in a CoCo violation. Whereas using Main as main component name would be valid since we specified the Main model above.

Incorrect:
<pre>
package example;

testdiagram MainTest for <b>Foo</b> {
}
</pre>
- [ERROR] 0xSD4CPT1010: Main Component Instance 'Foo' has no model file!

Correct:
<pre>
package example;

testdiagram MainTest for <b>Main</b> {
}
</pre>

## PortAccessValid

The [SD4CConnection](../../../../../../grammars/de/monticore/lang/Readme.md#sd4cconnection) connects ports inside the component and sub components. This CoCo verifies, that the Port names and component names (if provided) are resolvable.

In this example we trigger the CoCo failure by using port and component names, that are invalid. fooPort is not a port in Main and neither is fooCom a component specified in Main.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  -> <b>fooPort</b> : 12;
  <b>fooPort</b> -> sumCom.first, <b>fooCom</b>.second : 12;
}
</pre>
- [ERROR] 0xSD4CPT1040: The Port Access 'fooPort' could not be found!
- [ERROR] 0xSD4CPT1040: The Port Access 'fooPort' could not be found!
- [ERROR] 0xSD4CPT1030: ComponentInstance 'fooCom' of PortAccess 'fooCom.second' could not be found in MainComponent 'Main'!

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  -> <b>inPort</b> : 12;
  <b>inPort</b> -> <b>sumCom</b>.<b>first</b>, <b>sumCom</b>.<b>second</b> : 12;
}
</pre>

## SD4CConnectionConnectorValid

When defining a [SD4CConnection](../../../../../../grammars/de/monticore/lang/Readme.md#sd4cconnection) one must ensure that the connection must be an existing connection. 
Therefore this CoCo will lookup whether a matching connection exists.

Here we use a connection that is not specified in Main. At the first look one would think that `inPort -> outPort` is a valid connection, but since there is no connection from `inPort` directly to `outPort`, this connection is invalid.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  -> inPort : 12;
  <b>inPort -> outPort</b> : 12;
}
</pre>
- [ERROR] 0xSD4CPT1130: Connection 'inPort -> outPort : 12' is not defined in MainComponent 'Main' as connector

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  -> inPort : 12;
  <b>inPort -> sumCom.first</b> : 12;
}
</pre>

## SD4CConnectionValid

For a Connection to be valid, at least either source or target must be not empty. If both sides are empty it is simply an invalid connection.
However, if a target list is specified, the value list must match the amount of targets. With the exception that it is ok if there is only one value in the value list, then it does not matter how many targets there are, we use the same value for all targets.

In the example below there are two values, but only one target, which violates the rule.

Incorrect:
<pre>
package example;

testdiagram MainTestInvalid for Main {
  <b>-> : 12;</b>
  inPort -> sumCom.first : 12<b>, 12</b>;
}
</pre>
- [ERROR] 0xSD4CPT1050: Connection '-> : 12' is not valid
- [ERROR] 0xSD4CPT1060: Connection 'inPort -> sumCom.first : 12, 12' is not valid (wrong value amount)

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  <b>-> inPort : 12;</b>
  <b>inPort -> sumCom.first, sumCom.second : 12, 12;</b>
}
</pre>

## SD4CConnectionMainOutputValid

If the source of a connection is named and the target list is left empty, this CoCo looks up if source is an output of the main component. With an empty target list, we do not allow any other port type.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  <b>sumCom.first</b> -> : 24;
  <b>inPort</b> -> : 24;
}
</pre>
- [ERROR] 0xSD4CPT1070: Component 'sumCom' given in Output Connection 'sumCom.first -> : 24'
- [ERROR] 0xSD4CPT1080: Output Port 'inPort' of Connection 'inPort -> : 24' could not be found in MainComponent 'Main'

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  <b>outPort</b> -> : 24;
}
</pre>

## SD4CConnectionMainInputValid

In case the connection source is empty and the target list has at least one entry, this CoCo checks if all targets are input ports of the main component. 

In the example only output ports are in the target list, resulting in two CoCo violations. Even if another port of `sumCom`, like `sumCom.first` would be choosen, this connection would have resulted in an error, since the CoCo allows input ports of the main component only.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  -> <b>sumCom.outPort</b> : 12;
  -> <b>outPort</b> : 12;
}
</pre>
- [ERROR] 0xSD4CPT1090: Component 'sumCom' given in Input Connection '-> sumCom.outPort : 12'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection '-> outPort : 12' could not be found in MainComponent 'Main'

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  -> <b>inPort</b> : 12;
}
</pre>

## SD4CConnectionPortsDirectionValid

This CoCo handles the port direction if source as well as target list are specified. There are a few different cases of what port types could be connected.
The source must be an input port of the main component or an output port of any sub component and the target ports must be output ports of the main component or input ports of any sub component.
The following incorrect example shows all cases (limited to only one target in target list) where this is not the case.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  <b>inPort -> inPort</b> : 12;
  <b>inPort -> sumCom.outPort</b> : 12;
  <b>sumCom.outPort -> inPort</b> : 24;
  <b>sumCom.outPort -> sumCom.outPort</b> : 24;
  <b>outPort -> inPort</b> : 24;
  <b>outPort -> outPort</b> : 24;
  <b>outPort -> sumCom.first</b> : 24;
  <b>outPort -> sumCom.outPort</b> : 24;
  <b>sumCom.first -> inPort</b> : 24;
  <b>sumCom.first -> outPort</b> : 24;
  <b>sumCom.first -> sumCom.first</b> : 24;
  <b>sumCom.first -> sumCom.outPort</b> : 24;
}
</pre>
- [ERROR] 0xSD4CPT1080: Output Port 'inPort' of Connection 'inPort -> inPort : 12' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1120: Input Port 'outPort' of Connection 'inPort -> sumCom.outPort : 12' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1080: Output Port 'inPort' of Connection 'sumCom.outPort -> inPort : 24' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1120: Input Port 'outPort' of Connection 'sumCom.outPort -> sumCom.outPort : 24' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection 'outPort -> inPort : 24' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection 'outPort -> outPort : 24' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection 'outPort -> sumCom.first : 24' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection 'outPort -> sumCom.outPort : 24' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1110: Output Port 'first' of Connection 'sumCom.first -> inPort : 24' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1110: Output Port 'first' of Connection 'sumCom.first -> outPort : 24' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1110: Output Port 'first' of Connection 'sumCom.first -> sumCom.first : 24' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1110: Output Port 'first' of Connection 'sumCom.first -> sumCom.outPort : 24' could not be found in Component 'sumCom'

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  <b>inPort -> sumCom.first, sumCom.second</b> : 12;
  <b>sumCom.outPort -> outPort</b> : 24;
  <b>inPort -> outPort</b> : 24;
  <b>sumCom.outPort -> sumCom.first, sumCom.second</b> : 24;
}
</pre>

## CurrentlyNotSupported

There is currently no support in our C++ test generator for Expressions performing an Assignment or variable manipulation. This CoCo makes sure there is not.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  assert <b>i = 13</b>;
  assert <b>i++</b>;
  assert <b>++i</b>;
}
</pre>
- [ERROR] 0xSD4CPT1839: SD4Component testing currently not supporting assignment expressions
- [ERROR] 0xSD4CPT1839: SD4Component testing currently not supporting assignment expressions
- [ERROR] 0xSD4CPT1839: SD4Component testing currently not supporting assignment expressions


Correct:
<pre>
package example;

testdiagram MainTest for Main {
  assert <b>1 + 3 < 13</b>;
}
</pre>

## NameExpressionsAreResolvable

Expressions containing a Name are NameExpressions, this CoCo checks if the identifier name can be resolved.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  assert <b>doesnotexist</b> > 12;
}
</pre>
- [ERROR] MainTest.sd4c:<4,9>: 0xSD4CPT1510: The identifier 'doesnotexist' cannot be resolved.

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  assert <b>outPort</b> > 12;
}
</pre>

## OCLExpressionsValid

This CoCo restricts the usage of a lot of different OCL Expression types. Because there usage in our generator is not supported or only partly supported.

The example below only shows a few of them.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  assert forall a in b, c in d: 5;
  assert any 1;
  assert 1@pre;
  assert 1**;
  assert forall a in 1: 5;
}
</pre>
- [ERROR] 0xSD4CPT1826: Only one InDeclaration is supported for every ForallExpression
- [ERROR] 0xSD4CPT1833: Only SetEnumerations and SetComprehensions are supported as Expressions in InDeclarations of 'ForallExpression'
- [ERROR] 0xSD4CPT1828: Only SetEnumerations or SetComprehensions are allowed in AnyExpressions
- [ERROR] 0xSD4CPT1829: OCLAtPreQualification can only be applied to variables of components
- [ERROR] 0xSD4CPT1831: OCLTransitiveQualification is not supported
- [ERROR] 0xSD4CPT1833: Only SetEnumerations and SetComprehensions are supported as Expressions in InDeclarations of 'ForallExpression'

Correct:
<pre>
-
</pre>

## FieldAccessExpressionsAreResolvable

Field access must be names and they must be resolvable

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  assert <b>false</b>.first < 29;
  assert sumCom.<b>doesnotexist</b> < 29;
}
</pre>
- [ERROR] 0xSD4CPT1837: FieldAccessExpressions are only allowed to access Ports of Components
- [ERROR] 0xSD4CPT1838: 'doesnotexist' not found as FieldAccessExpression

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  assert <b>sumCom</b>.first < 29;
  assert sumCom.<b>outPort</b> < 29;
}
</pre>

## SD4CDelayGTZero


The [SD4CDelay](../../../../../../grammars/de/monticore/lang/Readme.md#sd4cdelay) is used to wait f fixed time during the execution of the C++ test before continuing with the next [SD4CExpression](../../../../../../grammars/de/monticore/lang/Readme.md#sd4cexpression). This CoCo checks if the delay time is actually not 0. 
We consider a delay that deliberately does not actually wait any time to be invalid.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  delay <b>0</b> min;
}
</pre>
- [ERROR] 0xSD4CPT1140: Delay 'delay 0 min' must be greater than zero.

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  delay <b>5</b> min;
}
</pre>

## SD4CDelayValidUnit

The [SD4CDelay](../../../../../../grammars/de/monticore/lang/Readme.md#sd4cdelay) expression contains the time as an `SIUnitLiteral`, meaning that the delay time is specified by a number with an unit. This CoCo only allows the usage of time units like h for hours, min for minutes, s for seconds, ms for milliseconds, μs for microseconds and ns for nanoseconds. Any other unit is considered invalid.

Incorrect:
<pre>
package example;

testdiagram MainTest for Main {
  delay 5 <b>km</b>;
}
</pre>
- [ERROR] 0xSD4CPT1150: The unit 'km' is unknown.

Correct:
<pre>
package example;

testdiagram MainTest for Main {
  delay 5 <b>ms</b>;
}
</pre>

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