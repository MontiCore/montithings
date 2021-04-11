// (c) https://github.com/MontiCore/monticore
package cocoTest.publishReferencesPort.invalid;

component Source {
  port out int value;

  int lastValue = 0;

  behavior {
    value = lastValue++;

    // The following statement is illegal
    publish lastValue;
  }

  update interval 1s;
}
