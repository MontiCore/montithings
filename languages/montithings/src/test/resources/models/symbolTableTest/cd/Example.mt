// (c) https://github.com/MontiCore/monticore
package cd;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;

  update interval 1s;
  timing sync;
}