// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Middleman {
  port in int input;
  port out int output;

  behavior {
    log("Middleman: " + input);
    output = input;
  }
}