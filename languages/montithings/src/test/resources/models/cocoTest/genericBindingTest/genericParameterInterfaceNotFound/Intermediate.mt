// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterInterfaceNotFound;

import cocoTest.genericBindingTest.genericParameterInterfaceNotFound.*;
import cocoTest.genericBindingTest.genericParameterInterfaceNotFound.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  T smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}
