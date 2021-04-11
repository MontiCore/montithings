// (c) https://github.com/MontiCore/monticore
package cocoTest.valid;

component Source {
  port out int value;

  int lastValue = 0;

  every 1s {
    value = lastValue++;
  }
}