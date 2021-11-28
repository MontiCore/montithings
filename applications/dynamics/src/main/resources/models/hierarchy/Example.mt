// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  port in CoDataProvider connect;
  port in CoDataProvider disconnect;
  Sink sink;

  behavior connect {
    connect.value -> sink.value;
  }

  behavior disconnect {
    disconnect.value -/> sink.value;
  }
}
