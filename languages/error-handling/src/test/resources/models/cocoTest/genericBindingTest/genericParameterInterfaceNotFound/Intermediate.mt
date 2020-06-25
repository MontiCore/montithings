// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterInterfaceNotFound;

import cocoTest.genericBindingTest.genericParameterInterfaceNotFound.*;
import cocoTest.genericBindingTest.genericParameterInterfaceNotFound.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
