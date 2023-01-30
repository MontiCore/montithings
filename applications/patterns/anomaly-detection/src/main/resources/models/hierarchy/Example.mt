// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Source2 source2;
  Middleman middleman;
  Sink sink;

  source.value -> middleman.input1;
  source2.value -> middleman.input2;
  middleman.output -> sink.value;
}