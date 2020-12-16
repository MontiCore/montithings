// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port in Integer sensor;
  port out Integer value;

  behavior {
    value = sensor?;
  }
}
