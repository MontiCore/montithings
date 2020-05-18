// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.valid;

import cocoTest.genericBindingTest.valid.*;
import cocoTest.genericBindingTest.valid.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
