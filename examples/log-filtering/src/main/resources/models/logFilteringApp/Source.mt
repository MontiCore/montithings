// (c) https://github.com/MontiCore/monticore
package logFilteringApp;

component Source {
  port in int sensor;
  port out int value;

  behavior {
    if(sensor?) {
      log("New sensor input: " + sensor);
      value = sensor?;
    }
  }

  update interval 1s;
}
