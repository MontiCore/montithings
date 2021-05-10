// (c) https://github.com/MontiCore/monticore
package cocoTest.dontReadOutports.invalidEvery;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
}