// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.valid;

import cocoTest.genericBindingTest.valid.*;
import cocoTest.genericBindingTest.valid.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  T smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}
