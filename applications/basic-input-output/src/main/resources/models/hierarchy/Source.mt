package hierarchy;

component Source {
  port out Integer value;

  int lastValue = 0;

  behavior {
    value = lastValue++;
  }
}
