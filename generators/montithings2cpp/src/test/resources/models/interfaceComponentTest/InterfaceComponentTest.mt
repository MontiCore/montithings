package interfaceComponentTest;

<<deploy>> component InterfaceComponentTest {
  component InComp inComp;
  component Interface impl;

  connect impl.outPort -> inComp.inPort;
}