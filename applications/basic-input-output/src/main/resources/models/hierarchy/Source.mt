// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port out int value;

  int lastValue = 0;

  behavior {
    value = lastValue++;
    delay(500);
    log("Source: " + lastValue);
  }

  update interval 1s;
}
