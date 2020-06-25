// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceNotFound;

import cocoTest.genericBindingTest.interfaceNotFound.*;
import cocoTest.genericBindingTest.interfaceNotFound.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
