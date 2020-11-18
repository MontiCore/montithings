// (c) https://github.com/MontiCore/monticore
package hierarchy;

application Example {
  Source source;
  LowPassFilter lpf (5, 0);
  Sink sink;

  source.value -> lpf.givenValue;
  lpf.filteredValue -> sink.value;

  timing sync;
}