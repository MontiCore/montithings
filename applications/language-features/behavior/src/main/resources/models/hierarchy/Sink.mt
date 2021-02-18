// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Sink {
  port in int value;

  behavior {
    #log "Sink: $value";
  }
}
