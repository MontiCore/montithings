// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port in int sensor;
  port out int value;

  behavior {
    value = sensor?;
  }
}
