// (c) https://github.com/MontiCore/monticore
package hierarchy;

application Example {
  Source source;
  Calc<Doubler> c;
  Sink sink;

  source.value -> c.x;
  c.y -> sink.value;

  timing sync;
}