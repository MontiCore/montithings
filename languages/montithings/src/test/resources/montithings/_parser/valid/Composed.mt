// (c) https://github.com/MontiCore/monticore
package valid;

component Composed {
  Source source;
  Sink sink;

  source.value -> sink.value;

  update interval 1s;
}