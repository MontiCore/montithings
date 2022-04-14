// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  LowPassFilter lpf (2, 0);
  Sink sink;

  source.value -> lpf.givenValue;
  lpf.filteredValue -> sink.value;
}