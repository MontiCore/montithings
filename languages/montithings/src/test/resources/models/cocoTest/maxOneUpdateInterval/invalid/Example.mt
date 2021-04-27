// (c) https://github.com/MontiCore/monticore
package cocoTest.maxOneUpdateInterval.invalid;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
}
