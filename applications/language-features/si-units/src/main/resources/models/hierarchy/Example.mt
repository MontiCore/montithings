// (c) https://github.com/MontiCore/monticore
package hierarchy;

application Example {
  Source source (1 km/h);
  Sink sink;

  source.value -> sink.value;
}