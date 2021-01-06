// (c) https://github.com/MontiCore/monticore
package cocoTest.nameExpressionsAreResolvableTest.variableDefaultUnknown;

application Example {
  Source source (0);
  Sink sink;

  source.value -> sink.value;
}
