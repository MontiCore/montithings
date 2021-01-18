// (c) https://github.com/MontiCore/monticore
package hierarchy;

application Example {
  Source source;
  RunningSum rs;
  Sink sink;

  source.value -> rs.input;
  rs.value -> sink.value;

  timing sync;
  update interval 1sec;
}