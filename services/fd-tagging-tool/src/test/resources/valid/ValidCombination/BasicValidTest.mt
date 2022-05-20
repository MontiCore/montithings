// (c) https://github.com/MontiCore/monticore
package valid.ValidCombination;

component BasicValidTest {
  Source source;
  Sink sink;

  source.value -> sink.value;
}