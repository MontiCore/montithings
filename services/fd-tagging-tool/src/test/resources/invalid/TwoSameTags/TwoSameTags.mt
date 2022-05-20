// (c) https://github.com/MontiCore/monticore
package invalid.TwoSameTags;

component TwoSameTags {
  Source source;
  Sink sink;

  source.value -> sink.value;
}