// (c) https://github.com/MontiCore/monticore
package cocoTest.valid;

component Converter {
  port in  int inport;
  port out int outport;

  behavior {
    outport = inport ?: 0;
  }
}