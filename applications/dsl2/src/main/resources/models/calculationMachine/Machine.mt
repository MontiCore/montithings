// (c) https://github.com/MontiCore/monticore
package calculationMachine;

component Machine {
  NumberGenerator numGen;
  Calculator calc;
  Printer print;

  numGen.value -> calc.value;
  calc.result -> print.result;
  print.text -> numGen.text;
}