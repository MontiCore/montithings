// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Sink sink;
  Three three;

  source.value -> three.input;
  three.output -> sink.value;
}