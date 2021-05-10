// (c) https://github.com/MontiCore/monticore
package cocoTest.loggedVariablesAreResolvable.invalid;

component Source {
  port out int value;

  int lastValue = 0;

  behavior {
    value = lastValue++;

    // The following statement is illegal
    log "Source: $unknownIdentifier";
  }

  update interval 1s;
}
