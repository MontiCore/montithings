// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.implementationMissing;

import cocoTest.genericBindingTest.implementationMissing.*;
import cocoTest.genericBindingTest.implementationMissing.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  T smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}
