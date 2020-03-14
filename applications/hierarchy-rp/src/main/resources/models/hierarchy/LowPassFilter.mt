package hierarchy;

component LowPassFilter (int threshold) {
  port in  int inport;
  port out int outport;

  behavior {
    if (inport < threshold)  : passthrough();
    else dismiss();
  }

}