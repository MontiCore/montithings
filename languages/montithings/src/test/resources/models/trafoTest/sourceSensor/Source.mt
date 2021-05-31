// (c) https://github.com/MontiCore/monticore
package sourceSensor;

component Source {
  port in int sensor;
  port out int value;

  behavior sensor {
    value = sensor;
  }
}
