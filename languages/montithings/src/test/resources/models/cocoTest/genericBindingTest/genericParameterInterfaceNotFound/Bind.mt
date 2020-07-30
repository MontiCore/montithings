// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterInterfaceNotFound;

import cocoTest.genericBindingTest.genericParameterInterfaceNotFound.*;

component Bind<T extends SmokeSensorInterface> {

  /* Subcomponents */
  Intermediate<T> intermediate;
  T smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}
