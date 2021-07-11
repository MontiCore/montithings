// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Sink {
  port in int value;

  behavior {
    if (value?) {
        log("Sink:" + value);
    }
  }
}
