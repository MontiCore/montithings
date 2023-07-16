// (c) https://github.com/MontiCore/monticore
package calculationMachine;

component Printer {
  port in CalculatorMessages.Result result;
  port out CalculatorMessages.PrintText text;
}
