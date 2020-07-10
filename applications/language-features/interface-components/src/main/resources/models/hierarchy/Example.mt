package hierarchy;

<<timesync>> application Example {
  Source source;
  Calc<Double> c;
  Sink sink;

  source.value -> c.x;
  c.y -> sink.value;

  update interval 1sec;

}