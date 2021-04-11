// (c) https://github.com/MontiCore/monticore
package cocoTest.dontReadOutports.invalidBehavior;

component Source {
  port out int value;

  int lastValue = 0;

  behavior {
    lastValue = value;
    value = lastValue++;
  }

  update interval 1s;
}
