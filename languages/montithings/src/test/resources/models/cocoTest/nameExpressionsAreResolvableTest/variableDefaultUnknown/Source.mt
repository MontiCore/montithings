// (c) https://github.com/MontiCore/monticore
package cocoTest.nameExpressionsAreResolvableTest.variableDefaultUnknown;

component Source (int start) {
  port out int value;

  int lastValue = unknownIdentifier;

  behavior {
    value = lastValue++;
  }

  update interval 1s;
}
