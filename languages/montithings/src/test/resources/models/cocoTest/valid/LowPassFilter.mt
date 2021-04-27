// (c) https://github.com/MontiCore/monticore
package cocoTest.valid;

component LowPassFilter (int threshold) {
  port in  int inport;
  port out int outport;

  behavior {
    if (inport < threshold)  outport = inport;
    else outport = 0;
  }

}