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
  } with test {

  }

  behavior disconnect {
    collector.remainingSpace -/> disconnect.fillableSpace;
    disconnect.pickUpSignal -/> collector.pickUpSignal;
    disconnect.trashType -/> collector.trashType;
  }
}