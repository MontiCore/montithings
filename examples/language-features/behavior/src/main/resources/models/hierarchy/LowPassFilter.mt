// (c) https://github.com/MontiCore/monticore
package hierarchy;

component LowPassFilter (int threshold, int defaultValue) {
  port in  int givenValue;
  port out int filteredValue;

  behavior {
    if (givenValue > threshold) filteredValue = defaultValue;
    else filteredValue = givenValue;
  }

}