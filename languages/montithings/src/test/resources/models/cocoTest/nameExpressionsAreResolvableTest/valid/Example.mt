// (c) https://github.com/MontiCore/monticore
package cocoTest.nameExpressionsAreResolvableTest.valid;

component Example {
  Source source (0);
  Sink sink;

  source.value -> sink.value;
}
