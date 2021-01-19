// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {
  port out m/s<double> value;

  int lastValue = 0;

  behavior {
    value = lastValue++;
  }

  update interval 1sec;
}
