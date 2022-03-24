// (c) https://github.com/MontiCore/monticore
package examples.correct;

component Main {
  port in int value;

  port out int foo;
  port out int foo2;

  Sum sumCom;
  Sum sumComp;

  value -> sumCom.first;
  value -> sumCom.second;
  sumCom.result -> foo;

  value -> sumComp.first;
  value -> sumComp.second;
  sumComp.result -> foo2;
}
