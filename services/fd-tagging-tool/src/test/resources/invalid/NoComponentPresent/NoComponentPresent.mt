// (c) https://github.com/MontiCore/monticore
package invalid.NoComponentPresent;

component NoComponentPresent {
  Source source;
  Sink sink;

  source.value -> sink.value;
}