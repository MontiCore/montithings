package hierarchy;

component Converter {
  port in  int inport;
  port out int outport;

  behavior {
    if (inport == --) : setZero();
    else propagate();
  }
}