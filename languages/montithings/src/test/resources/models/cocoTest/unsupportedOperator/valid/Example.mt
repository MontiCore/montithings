// (c) https://github.com/MontiCore/monticore
package cocoTest.unsupportedOperator.valid;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
}