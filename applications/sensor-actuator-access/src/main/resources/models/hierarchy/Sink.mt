// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Sink {
  port in Integer value;
  port out Integer actuator;

  behavior {
    actuator = value;
  }
}
