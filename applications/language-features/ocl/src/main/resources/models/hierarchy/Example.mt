// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  RunningSum rs;
  Sink sink;

  source.value -> rs.in;
  rs.result -> sink.value;
}