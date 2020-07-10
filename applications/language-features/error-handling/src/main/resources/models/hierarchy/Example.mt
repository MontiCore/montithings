package hierarchy;

<<timesync>> application Example {
  Source source;
  Sink sink;

  source.value -> sink.value;

  update interval 1sec;
}