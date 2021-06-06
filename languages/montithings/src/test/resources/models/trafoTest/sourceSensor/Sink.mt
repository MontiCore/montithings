// (c) https://github.com/MontiCore/monticore
package sourceSensor;

component Sink {
  port in int value;
  port out int actuator;

  behavior {
    actuator = value?;
  }
}
