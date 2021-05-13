// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port out int value;

  int lastValue = 0;

  behavior {
    value = lastValue++;
    log("Source | out: " + lastValue);
  }

  update interval 1s;
}
