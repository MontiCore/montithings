// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source, source2;
  MathExpression c,c2;
  Sink sink, sink2;
  GenericBinding<Doubler> genericBinding;

  source.value -> c.x;
  c.y -> sink.value;
  source2.value -> c2.x;
  c2.y -> sink2.value;

  timing sync;
}