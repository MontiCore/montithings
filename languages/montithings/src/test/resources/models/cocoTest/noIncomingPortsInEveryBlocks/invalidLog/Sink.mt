// (c) https://github.com/MontiCore/monticore
package cocoTest.noIncomingPortsInEveryBlocks.invalidLog;

component Sink {
  port in int value;

  every 2s {
    // the following statement is illegal
    log "Sink: $value";
  }
}
