// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port out int value;

  int lastValue = 0;

  every 1s {
    lastValue++;
    log("Source: " + lastValue);
    value = lastValue;
  }

  init {
    log("SourceInit: " + value);
    value = 40;
    log("SourceInit: " + value);
  }
}
