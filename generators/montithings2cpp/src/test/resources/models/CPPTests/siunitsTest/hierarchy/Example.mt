// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example /*(km/h sadge, int testint)*/ {
  Source source;
  Sink sink;

  km<double> test = 4 km;

  source.value -> sink.value;

  timing sync;
}