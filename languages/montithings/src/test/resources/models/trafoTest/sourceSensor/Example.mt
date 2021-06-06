// (c) https://github.com/MontiCore/monticore
package sourceSensor;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
}