package hierarchy;

component Converter {
  port in  int inport;
  port out int outport;

  behavior {
    if (inport == --) : outport = 0;
    else propagate();
  }
}