// (c) https://github.com/MontiCore/monticore
package invalid.TwoSameTags;

component Source {
  port out int value;

  int lastValue = 0;

  every 1s {
    lastValue++;
    log("Source: " + lastValue);
    value = lastValue;
  }
}
