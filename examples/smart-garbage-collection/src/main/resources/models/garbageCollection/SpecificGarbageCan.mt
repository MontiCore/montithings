// (c) https://github.com/MontiCore/monticore
package garbageCollection;

component SpecificGarbageCan implements AbstractGarbageCan {
  GarbageCan garbageCan;
  GarbageAdder garbageAdder;

  port in int fillableSpace;
  port out int pickUpSignal;
  port out int trashType;

  fillableSpace -> garbageCan.fillableSpace;
  garbageCan.pickUpSignal -> pickUpSignal;
  garbageCan.trashType -> trashType;

  garbageAdder.addGarbage -> garbageCan.newGarbage;
}