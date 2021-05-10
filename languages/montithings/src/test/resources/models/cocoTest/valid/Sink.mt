// (c) https://github.com/MontiCore/monticore
package cocoTest.valid;

component Sink {

  port in int value;

  behavior {
    log "Sink: $value";
  }

}
