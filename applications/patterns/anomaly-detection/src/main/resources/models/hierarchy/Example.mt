// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Source source2;
  Middleman middleman;
  Sink sink;

  source.value -> middleman.input;
  source2.value -> middleman.input;
  middleman.output -> sink.value;
}