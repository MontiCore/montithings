package invalid.TwoSameTags;

component TwoSameTags {
  Source source;
  Sink sink;

  source.value -> sink.value;
}