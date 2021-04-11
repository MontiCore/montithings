// (c) https://github.com/MontiCore/monticore

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
}