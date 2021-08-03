// (c) https://github.com/MontiCore/monticore
package logFilteringApp;

component Sink {
  port in int value;
  port out int actuator;

  behavior {
    if(value?) {
      actuator = value;
      log("actuator = " + value);
    }
  }
}
