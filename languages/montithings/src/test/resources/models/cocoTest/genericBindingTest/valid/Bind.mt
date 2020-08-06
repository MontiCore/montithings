// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.valid;

import cocoTest.genericBindingTest.valid.*;
import cocoTest.genericBindingTest.valid.sensors.*;

component Bind<T extends SmokeSensorInterface, T2 extends IntermediateInterface> {

  /* Subcomponents */
  T2 intermediate;
  T smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}
