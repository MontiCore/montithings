// (c) https://github.com/MontiCore/monticore
package cocoTest.postcondUsesOnlyOneOutport.invalid;

component Example {
  Source source;
  Sink sink;

  source.value -> sink.value;
  source.value2 -> sink.value2;
}
