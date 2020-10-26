// (c) https://github.com/MontiCore/monticore
package hierarchy;

application Example {
  Source source;
  Sink sink;

  source.value -> sink.value;

  update interval 1sec;
  timing sync;
}