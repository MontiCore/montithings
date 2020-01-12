// (c) https://github.com/MontiCore/monticore
package interfaceComponentTest;

<<deploy>> component InterfaceComponentTest {
  component InComp inComp;
  component Interface impl;

  connect impl.outPort -> inComp.inPort;
}