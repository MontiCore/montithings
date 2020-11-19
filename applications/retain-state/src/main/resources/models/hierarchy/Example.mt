// (c) https://github.com/MontiCore/monticore
package hierarchy;

application Example {
  Source source;
  Sink sink;

  source.value -> sink.value;

  timing sync;
  update interval 1sec;
}