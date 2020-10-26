// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Half {
  port in int x;
  port out int y;

  Sum sum;

  x -> sum.in1;
  x -> sum.in2;
  sum.result -> y;
}