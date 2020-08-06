package hierarchy;

application Example {
  Source source, source2;
  MathExpression c,c2;
  Sink sink, sink2;
  GenericBinding<Double> genericBinding;

  source.value -> c.x;
  c.y -> sink.value;
  source2.value -> c2.x;
  c2.y -> sink2.value;

  update interval 1sec;
  timing sync;
}