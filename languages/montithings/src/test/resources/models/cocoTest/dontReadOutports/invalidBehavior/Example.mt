// (c) https://github.com/MontiCore/monticore
package cocoTest.dontReadOutports.invalidBehavior;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
}