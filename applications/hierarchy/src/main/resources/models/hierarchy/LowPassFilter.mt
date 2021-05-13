// (c) https://github.com/MontiCore/monticore
package hierarchy;

component LowPassFilter (int threshold) {
  port in  int inport;
  port out int outport;

  behavior {
    if (inport < threshold)  outport = inport;
    else {
      log("LowPassFilter | out: 0 due to threshold");
      outport = 0;
    }
  }

}