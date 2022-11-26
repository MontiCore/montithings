// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Storage storage;

  source.value -> storage.value;
}