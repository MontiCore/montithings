package interfaceComponentTest;

<<deploy>> component InterfaceComponentTest {
  component InComp inComp;
  component Implementation impl;

  connect impl.outPort -> inComp.inPort;
}