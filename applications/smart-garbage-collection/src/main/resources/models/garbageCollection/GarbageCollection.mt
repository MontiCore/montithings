package garbageCollection;

component GarbageCollection {
  port in CoAbstractGarbageCan connect;
  port in CoAbstractGarbageCan disconnect;

  GarbageCollector collector;
  GarbageRemover remover;

  collector.remove -> remover.remove;

  behavior connect {
    collector.remainingSpace -> connect.fillableSpace;
    connect.pickUpSignal -> collector.pickUpSignal;
    connect.trashType -> collector.trashType;
  } test {
    fillableSpace = 70;
    wait 1s;
    assert pickUpSignal == 1;
  }
}