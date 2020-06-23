package valid;

application Composed {
  Source source;
  Sink sink;

  source.value -> sink.value;

  update interval 1sec;
}