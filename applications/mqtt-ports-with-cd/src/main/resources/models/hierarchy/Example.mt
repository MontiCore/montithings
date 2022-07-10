// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Doubler doubler;
  // Inverter inverter;
  Sink sink;

  source.value -> doubler.value;
  // doubler.doubled_value -> inverter.value;
  // inverter.inverted_value -> sink.value;
  doubler.doubled_value -> sink.value;
}