package hierarchy;

<<timesync>> application Example {
  component Source source;
  component LowPassFilter (5, 0) lpf;
  component Converter c;
  component Sink sink;

  connect source.value -> lpf.givenValue;
  connect lpf.filteredValue -> c.inport;
  connect c.outport -> sink.value;

  control {
    update interval 1sec;
  }
}