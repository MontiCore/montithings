// (c) https://github.com/MontiCore/monticore
package valid.PossibleConfiguration;

component PossibleConfiguration {
  Source source;
  Sink sink;

  source.value -> sink.value;
}