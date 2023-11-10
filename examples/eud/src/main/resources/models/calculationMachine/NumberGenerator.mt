// (c) https://github.com/MontiCore/monticore
package calculationMachine;

component NumberGenerator {
  port out CalculatorMessages.Variable value;
  port in CalculatorMessages.PrintText text;

  int lastValue = 0;

  every 1s {
    lastValue++;
    log("Variable: " + lastValue);

    value = :CalculatorMessages.Variable{
      val = lastValue;
    };
  }

  behavior text {
  log("Resulting text:" + text.text);
  }
}
