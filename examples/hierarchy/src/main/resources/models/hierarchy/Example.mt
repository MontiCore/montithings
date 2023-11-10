// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Sink sink;
  LowPassFilter lpf (5);
  Converter c;
  Doubler d;

  source.value -> lpf.inport;
  lpf.outport -> c.inport;
  c.outport -> d.x;
  d.y -> sink.value;
}