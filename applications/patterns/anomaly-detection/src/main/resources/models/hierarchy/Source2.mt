// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source2 {
  port out int value;

  int lastValue = 0;

  every 1s {
    lastValue++;
    log("Source2: " + lastValue);
    value = lastValue;
  }
}
