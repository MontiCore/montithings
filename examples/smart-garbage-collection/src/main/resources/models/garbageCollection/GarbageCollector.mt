// (c) https://github.com/MontiCore/monticore
package garbageCollection;

component GarbageCollector {
  port out int remainingSpace;
  port in int pickUpSignal;
  port in int trashType;

  port out int remove;

  int maxFillLevel = 100;
  int currentFillLevel = 0;

  behavior trashType {
    log("Trash Type of connected garbage can :" + trashType);
    remainingSpace = maxFillLevel - currentFillLevel;
  }

  behavior pickUpSignal {
    log("Garbage Collector is on its way to pick up trash can with Fill Level: " + pickUpSignal);
    currentFillLevel = currentFillLevel + pickUpSignal;
  }

  every 20s {
    remove = currentFillLevel;
    currentFillLevel = 0;
  }

  every 4s {
    remainingSpace = maxFillLevel - currentFillLevel;
  }
}