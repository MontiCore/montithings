// (c) https://github.com/MontiCore/monticore
// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Sink {
  port in String value;

  behavior value {
    log("Sink: " + value);
  }
}
