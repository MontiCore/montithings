// (c) https://github.com/MontiCore/monticore
package garbageCollection;

component GarbageAdder {
  port out int addGarbage;

  every 1s {
    addGarbage = 1;
  }
}