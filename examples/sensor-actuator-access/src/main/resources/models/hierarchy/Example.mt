// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Sink sink;
  Source source2;
  Sink sink2;

  source.value -> sink.value;
  source2.value -> sink2.value;
}