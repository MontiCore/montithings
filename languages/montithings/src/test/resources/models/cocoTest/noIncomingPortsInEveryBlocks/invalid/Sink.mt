// (c) https://github.com/MontiCore/monticore
package cocoTest.noIncomingPortsInEveryBlocks.invalid;

component Sink {
  port in int value;

  every 2s {
    // the following statement is illegal
    value += 0;
  }
}
