// (c) https://github.com/MontiCore/monticore
package cocoTest.nameExpressionsAreResolvableTest.valid;

application Example {
  Source source (0);
  Sink sink;

  source.value -> sink.value;
}
