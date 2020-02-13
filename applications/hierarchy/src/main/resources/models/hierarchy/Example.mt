package hierarchy;

<<deploy, timesync>> component Example {
  component Source source;
  component Sink sink;
  component LowPassFilter (5) lpf;
  component Converter c;
  component Double d;

  connect source.value -> lpf.inport;
  connect lpf.outport -> c.inport;
  connect c.outport -> d.x;
  connect d.y -> sink.value;

  control {
    update interval 1s;
  }

}