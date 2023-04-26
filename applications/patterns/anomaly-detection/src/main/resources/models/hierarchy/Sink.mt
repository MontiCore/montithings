// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Sink {
  port in int value;

  behavior value {
    log("Sink: " + value);
  }
}
