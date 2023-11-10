// (c) https://github.com/MontiCore/monticore
package logFilteringApp;

component Example {
  Source source;
  RunningSum runningSum;
  Doubler doubler;
  Sink sink1;
  Sink sink2;

  source.value -> doubler.x;
  doubler.y -> sink1.value;

  source.value -> runningSum.input;
  runningSum.value -> sink2.value;
}