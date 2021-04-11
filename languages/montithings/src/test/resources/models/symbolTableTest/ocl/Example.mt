// (c) https://github.com/MontiCore/monticore
package ocl;

component Example {
  Source source;
  RunningSum rs;
  Sink sink;

  source.value -> rs.in;
  rs.result -> sink.value;
}