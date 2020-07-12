package hierarchy;

application Example {
  Source source;
  Sink sink;
  LowPassFilter lpf (5);
  Converter c;
  Double d;

  source.value -> lpf.inport;
  lpf.outport -> c.inport;
  c.outport -> d.x;
  d.y -> sink.value;

  update interval 1sec;
  timing sync;
}