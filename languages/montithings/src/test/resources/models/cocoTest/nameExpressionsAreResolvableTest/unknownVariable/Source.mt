// (c) https://github.com/MontiCore/monticore
package cocoTest.nameExpressionsAreResolvableTest.unknownVariable;

component Source (int start) {
  port out int value;

  int lastValue = start;

  behavior {
    value = unknownIdentifier;
  }

  update interval 1s;
}
