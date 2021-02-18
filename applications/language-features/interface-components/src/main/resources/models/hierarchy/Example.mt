// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Calc<Doubler> c;
  Sink sink;

  source.value -> c.x;
  c.y -> sink.value;

  update interval 1s;
  timing sync;
}