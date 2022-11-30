// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  Rest rest;

  source.value -> rest.value;
}