// (c) https://github.com/MontiCore/monticore
package cocoTest.unsupportedOperator.invalidNotSimilar;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
}
