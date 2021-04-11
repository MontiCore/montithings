// (c) https://github.com/MontiCore/monticore
package cocoTest.maxOneUpdateInterval.invalid;

component Source {
  port out int value;

  int lastValue = 0;

  behavior {
    value = lastValue++;
  }

  every 100h {
    value = lastValue++;
  }

  // Having two update intervals is forbidden
  update interval 1s;
  update interval 1s;
}
