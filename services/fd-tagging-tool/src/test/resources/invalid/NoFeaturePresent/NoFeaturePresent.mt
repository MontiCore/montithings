// (c) https://github.com/MontiCore/monticore
package invalid.NoFeaturePresent;

component NoFeaturePresent {
  Source source;
  Sink sink;

  source.value -> sink.value;
}