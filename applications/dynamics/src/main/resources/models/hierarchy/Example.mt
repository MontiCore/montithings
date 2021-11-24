// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  port in CoDataProvider dp;
  port in CoDataProvider disconnect;
  Sink sink;

  behavior dp {
    dp.value -> sink.value;
  }

  behavior disconnect {
    disconnect.value -/> sink.value;
  }
}