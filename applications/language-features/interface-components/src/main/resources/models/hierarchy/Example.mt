// (c) https://github.com/MontiCore/monticore
package hierarchy;

application Example {
  Source source;
  Calc<Double> c;
  Sink sink;

  source.value -> c.x;
  c.y -> sink.value;

  update interval 1sec;
  timing sync;
}