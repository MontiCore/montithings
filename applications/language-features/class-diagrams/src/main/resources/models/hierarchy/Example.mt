package hierarchy;

<<timesync>> application Example {
  component Source source;
  component Sink sink;

  connect source.value -> sink.value;

  control {
    update interval 1sec;
  }
}