// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port out int value;

  int lastValue = 0;

  every 500ms {
    value = lastValue++;
    log "Source: $value";
  }
}
