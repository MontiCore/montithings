// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.implementationMissing;

import cocoTest.genericBindingTest.implementationMissing.*;
import cocoTest.genericBindingTest.implementationMissing.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
