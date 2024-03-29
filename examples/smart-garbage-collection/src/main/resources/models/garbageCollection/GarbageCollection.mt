// (c) https://github.com/MontiCore/monticore
package garbageCollection;

component GarbageCollection {
  port in CoAbstractGarbageCan connect;

  GarbageCollector collector;
  GarbageRemover remover;

  collector.remove -> remover.remove;

  behavior connect {
    collector.remainingSpace -> connect.fillableSpace;
    connect.pickUpSignal -> collector.pickUpSignal;
    connect.trashType -> collector.trashType;
  } test {
    fillableSpace = 70;
    wait 2s;
    assert pickUpSignal >= 1;
  }
}