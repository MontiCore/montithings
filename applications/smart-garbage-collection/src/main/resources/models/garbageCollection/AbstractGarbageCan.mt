package garbageCollection;

interface component AbstractGarbageCan {
  port in int fillableSpace;
  port out int pickUpSignal;
  port out int trashType;

  int typeOfTrash = 2;
  int fillLevel = 0;

  test (fillLevel:30) {
    //fillableSpace: 20;
    assert pickUpSignal == 0;
  }
}