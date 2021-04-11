// (c) https://github.com/MontiCore/monticore
package cocoTest.dontReadOutports.invalidEvery;

component Source {
  port out int value;

  int lastValue = 0;

  every 5s {
    lastValue = value;
    value = lastValue++;
  }
}
