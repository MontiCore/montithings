# Context Conditions

[...]

- The testdiagram's main component requires the corresponding MontiArc model ([MainComponentExists](#maincomponentexists))
- A Connection requires:
    - The named Component and it's ports ([PortAccessValid](#portaccessvalid))
    - The Connection's definition in the Component ([SD4CConnectionConnectorValid](#sd4cconnectionconnectorvalid))
- Connections are plausible:
    - Source or target are not empty ([SD4CConnectionValid](#sd4cconnectionvalid))
    - Source named and target empty: source must be Output of Main Component ([SD4CConnectionMainOutputValid](#sd4cconnectionmainoutputvalid))
    - Source empty and target named: all targets must be Input of Main Component ([SD4CConnectionMainInputValid](#sd4cconnectionmaininputvalid))
    - Source and target named: Source must be Input of Main Component or Output of a SubComponent and target must be Output of Main Component or Input of SubComponent ([SD4CConnectionPortsDirectionValid](#sd4cconnectionportsdirectionvalid))

- ([CurrentlyNotSupported](#currentlynotsupported))
- ([NameExpressionsAreResolvable](#nameexpressionsareresolvable))
- ([OCLExpressionsValid](#oclexpressionsvalid))
- ([FieldAccessExpressionsAreResolvable](#fieldaccessexpressionsareresolvable))
- ([SD4CDelayGTZero](#sd4cdelaygtzero))
- ([SD4CDelayValidUnit](#sd4cdelayvalidunit))

## Models used in examples

For the following examples, we use these models for reference, which would be found in our model path.

```
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
component Sum {
  port in int first;
  port in int second;

  port out int outPort;
}
```

## MainComponentExists

Incorrect:
<pre>
testdiagram MainTest for <b>Foo</b> {
}
</pre>
- [ERROR] 0xSD4CPT1010: Main Component Instance 'Foo' has no model file!

Correct:
<pre>
testdiagram MainTest for <b>Main</b> {
}
</pre>

## PortAccessValid

Incorrect:
<pre>
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
testdiagram MainTest for Main {
  -> <b>inPort</b> : 12;
  <b>inPort</b> -> <b>sumCom</b>.<b>first</b>, <b>sumCom</b>.<b>second</b> : 12;
}
</pre>

## SD4CConnectionConnectorValid

Incorrect:
<pre>
testdiagram MainTest for Main {
  -> inPort : 12;
  <b>inPort -> outPort</b> : 12;
}
</pre>
- [ERROR] 0xSD4CPT1130: Connection 'inPort -> outPort : 12;' is not defined in MainComponent 'Main' as connector

Correct:
<pre>
testdiagram MainTest for Main {
  -> inPort : 12;
  <b>inPort -> sumCom.first</b> : 12;
}
</pre>

## SD4CConnectionValid

Incorrect:
<pre>
testdiagram MainTestInvalid for Main {
  <b>-> : 12;</b>
  inPort -> sumCom.first : 12, 12;
}
</pre>
- [ERROR] 0xSD4CPT1050: Connection '-> : 12;' is not valid
- [ERROR] 0xSD4CPT1060: Connection 'inPort -> sumCom.first : 12, 12;' is not valid (wrong value amount)

Correct:
<pre>
testdiagram MainTest for Main {
  <b>-> inPort : 12;</b>
  <b>inPort -> sumCom.first, sumCom.second : 12, 12;</b>
}
</pre>

## SD4CConnectionMainOutputValid

Incorrect:
<pre>
testdiagram MainTest for Main {
  <b>inPort</b> -> : 24;
}
</pre>
- [ERROR] 0xSD4CPT1080: Output Port 'inPort' of Connection 'inPort -> : 24;' could not be found in MainComponent 'Main'

Correct:
<pre>
testdiagram MainTest for Main {
  <b>outPort</b> -> : 24;
}
</pre>

## SD4CConnectionMainInputValid

Incorrect:
<pre>
testdiagram MainTest for Main {
  -> <b>outPort</b> : 12;
}
</pre>
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection '-> outPort : 12;' could not be found in MainComponent 'Main'

Correct:
<pre>
testdiagram MainTest for Main {
  -> <b>inPort</b> : 12;
}
</pre>

## SD4CConnectionPortsDirectionValid

Incorrect:
<pre>
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
- [ERROR] 0xSD4CPT1080: Output Port 'inPort' of Connection 'inPort -> inPort : 12;' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1120: Input Port 'outPort' of Connection 'inPort -> sumCom.outPort : 12;' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1080: Output Port 'inPort' of Connection 'sumCom.outPort -> inPort : 24;' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1120: Input Port 'outPort' of Connection 'sumCom.outPort -> sumCom.outPort : 24;' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection 'outPort -> inPort : 24;' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection 'outPort -> outPort : 24;' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection 'outPort -> sumCom.first : 24;' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1100: Input Port 'outPort' of Connection 'outPort -> sumCom.outPort : 24;' could not be found in MainComponent 'Main'
- [ERROR] 0xSD4CPT1110: Output Port 'first' of Connection 'sumCom.first -> inPort : 24;' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1110: Output Port 'first' of Connection 'sumCom.first -> outPort : 24;' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1110: Output Port 'first' of Connection 'sumCom.first -> sumCom.first : 24;' could not be found in Component 'sumCom'
- [ERROR] 0xSD4CPT1110: Output Port 'first' of Connection 'sumCom.first -> sumCom.outPort : 24;' could not be found in Component 'sumCom'

Correct:
<pre>
testdiagram MainTest for Main {
  <b>inPort -> sumCom.first, sumCom.second</b> : 12;
  <b>sumCom.outPort -> outPort</b> : 24;
  <b>inPort -> outPort</b> : 24;
  <b>sumCom.outPort -> sumCom.first, sumCom.second</b> : 24;
}
</pre>

## CurrentlyNotSupported

Incorrect:
<pre>
</pre>
Correct:
<pre>
</pre>

## NameExpressionsAreResolvable

Incorrect:
<pre>
</pre>
Correct:
<pre>
</pre>

## OCLExpressionsValid

Incorrect:
<pre>
</pre>
Correct:
<pre>
</pre>

## FieldAccessExpressionsAreResolvable

Incorrect:
<pre>
</pre>
Correct:
<pre>
</pre>

## SD4CDelayGTZero

Incorrect:
<pre>
</pre>
Correct:
<pre>
</pre>

## SD4CDelayValidUnit

Incorrect:
<pre>
</pre>
Correct:
<pre>
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