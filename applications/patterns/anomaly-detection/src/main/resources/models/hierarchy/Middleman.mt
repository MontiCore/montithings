// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Middleman {
  port in int input1;
  port in int input2;
  port out int output;

  behavior {
    log("Middleman Input 1: " + input1);
    log("Middleman Input 2: " + input2);
    output = input1 + input2;
  }
}