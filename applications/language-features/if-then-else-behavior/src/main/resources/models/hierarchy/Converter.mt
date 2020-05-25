package hierarchy;

component Converter {
  port in  int inport;
  port out int outport;

  behavior {
    // "--" means "No Data"
    if (inport == --) : outport = 0;

    // "propagate()" is implemented using hand-written code
    else propagate();
  }
}