// (c) https://github.com/MontiCore/monticore
package cocoTest.dontReadOutports.valid;

component Source {
  port out int value;

  int lastValue = 0;

  behavior {
    value = lastValue++;
  }

  every 100h {
    value = lastValue++;
  }

  update interval 1s;
}
