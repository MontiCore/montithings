// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Example {
  Source source;
  ChooseNext cn;
  Sink sink;

  source.value -> cn.input;
  cn.output -> sink.value;
}