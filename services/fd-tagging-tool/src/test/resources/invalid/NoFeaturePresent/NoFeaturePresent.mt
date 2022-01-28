package invalid.NoFeaturePresent;

component NoFeaturePresent {
  Source source;
  Sink sink;

  source.value -> sink.value;
}