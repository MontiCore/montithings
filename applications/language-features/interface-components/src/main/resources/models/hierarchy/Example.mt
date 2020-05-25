package hierarchy;

<<timesync>> application Example {
  component Source source;
  component Calc<Double> c;
  component Sink sink;

  connect source.value -> c.x;
  connect c.y -> sink.value;

  control {
    update interval 1sec;
  }

}