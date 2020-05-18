// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterInterfaceNotFound;

import cocoTest.genericBindingTest.genericParameterInterfaceNotFound.*;

component Bind<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component Intermediate<T> intermediate;
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
