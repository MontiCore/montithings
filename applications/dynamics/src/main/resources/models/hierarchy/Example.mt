// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  port in CoDataProvider dp;
  Sink sink;

  behavior dp {
    dp.value -> sink.value;
  }
}