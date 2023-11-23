// (c) https://github.com/MontiCore/monticore
package garbageCollection;

component GarbageRemover {
  port in int remove;

  behavior {
    log("Removed " + remove + " garbage units.");
  }
}