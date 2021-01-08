// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Sum {
  port in int in1;
  port in int in2;
  sync in1, in2;

  port out int result;
}