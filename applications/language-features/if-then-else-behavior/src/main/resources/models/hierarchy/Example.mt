package hierarchy;

<<timesync>> application Example {
  Source source;
  LowPassFilter (5, 0) lpf;
  Converter c;
  Sink sink;

  source.value -> lpf.givenValue;
  lpf.filteredValue -> c.inport;
  c.outport -> sink.value;

  update interval 1sec;
}