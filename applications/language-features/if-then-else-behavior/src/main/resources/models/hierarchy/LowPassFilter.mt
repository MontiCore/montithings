package hierarchy;

component LowPassFilter (int threshold, int defaultValue) {
  port in  int givenValue;
  port out int filteredValue;

  assume : givenValue >= 0;

  behavior {
    if (givenValue > threshold)  : filteredValue = defaultValue;
    else filteredValue = givenValue;
  }

  guarantee  : filteredValue <= threshold;
}