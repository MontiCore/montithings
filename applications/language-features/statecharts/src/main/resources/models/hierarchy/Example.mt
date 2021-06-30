// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Three three;

  source.value -> three.input;
}