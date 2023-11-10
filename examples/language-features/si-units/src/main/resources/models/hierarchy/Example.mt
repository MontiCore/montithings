// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source (1 km/h);
  Sink sink;

  source.value -> sink.value;
}