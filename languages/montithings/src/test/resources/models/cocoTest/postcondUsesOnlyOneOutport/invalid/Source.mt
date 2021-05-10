// (c) https://github.com/MontiCore/monticore
package cocoTest.postcondUsesOnlyOneOutport.invalid;

component Source {
  port out int value;
  port out int value2;

  // The following statement is illegal
  post value < 5 && value2 < 6;

  int lastValue = 0;

  behavior {
    value = lastValue++;
  }

  update interval 1s;
}
