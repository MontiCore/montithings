// (c) https://github.com/MontiCore/monticore
package cocoTest.unsupportedOperator.invalidNotSimilar;

component Source {
  port out int value;

  int lastValue = 0;

  behavior {
    value = lastValue++;

    // the following statement is illegal
    lastValue ?!~ value;
  }

  every 100h {
    value = lastValue++;
  }

  update interval 1s;
}
