package garbageCollection;

component TestGarbageCan implements AbstractGarbageCan {
  port in int fillableSpace;
  port out int pickUpSignal;
  port out int trashType;

  // TODO AKL port for signaling when pickUp is done

  int typeOfTrash = 2;
  int fillLevel = 20;

  init {
    trashType = typeOfTrash;
  }

  behavior fillableSpace {
    if (fillLevel > 10 && fillLevel <= fillableSpace) {
      after 1s {
        pickUpSignal = 1;
        fillLevel = 0;
      }
    } else if (fillLevel < 10) {
      log("Fill Level of Garbage Can is less than 10, should not be picked up");
    } else {
      log("Garbage Can with fill level " + fillLevel + " wants to be picked up, but remaining space " + fillableSpace + " in Garbage Collector is not enough");
    }
  }

  every 1s {
    fillLevel += 1;
    log("Added new Garbage, fill level is now: " + fillLevel);
  }
}