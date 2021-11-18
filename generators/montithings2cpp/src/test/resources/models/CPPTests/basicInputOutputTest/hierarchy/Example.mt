// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Sink sink;

  port in Source s;
  port in int test;

  source.value -> sink.value;

  timing sync;

  behavior s {
    value -> test;
  }
}