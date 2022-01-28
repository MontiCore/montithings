package valid.ValidCombination;

component BasicValidTest {
  Source source;
  Sink sink;

  source.value -> sink.value;
}