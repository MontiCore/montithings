// (c) https://github.com/MontiCore/monticore
package cocoTest.noIncomingPortsInEveryBlocks.invalidLog;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
}
