// (c) https://github.com/MontiCore/monticore
package hierarchy;

application Example /*(km/h sadge, int testint)*/ {
  Source source;
  Sink sink;

  km<double> test = 4 km;

  source.value -> sink.value;

  behavior {
    km/h<float> testBehavior;
    testBehavior = 53 km;
  }

  timing sync;
}