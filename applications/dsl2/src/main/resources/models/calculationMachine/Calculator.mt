// (c) https://github.com/MontiCore/monticore
package calculationMachine;

component Calculator {
  port in CalculatorMessages.Variable value;
  port out CalculatorMessages.Result result;
}
