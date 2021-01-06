// (c) https://github.com/MontiCore/monticore
package cocoTest.nameExpressionsAreResolvableTest.unknownVariable;

application Example {
  Source source (0);
  Sink sink;

  source.value -> sink.value;
}
