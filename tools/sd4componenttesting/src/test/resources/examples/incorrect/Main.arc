// (c) https://github.com/MontiCore/monticore
package examples.incorrect;

component Main {
  port in int value;

  port out int result;

  Sum sumCom;

  value -> sumCom.first;
  value -> sumCom.second;
  sumCom.result -> result;
}
