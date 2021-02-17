// (c) https://github.com/MontiCore/monticore
package cocoTest.nameExpressionsAreResolvableTest.valid;

component Source (int start) {
  port out int value;

  int lastValue = start;

  behavior {
    value = lastValue++;
  }

  update interval 1s;
}
