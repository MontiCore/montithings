// (c) https://github.com/MontiCore/monticore
package cocoTest.nameExpressionsAreResolvableTest.variableDefaultUnknown;

component Example {
  Source source (0);
  Sink sink;

  source.value -> sink.value;
}
