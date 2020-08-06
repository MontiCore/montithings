// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNeedsInterface;

import cocoTest.genericBindingTest.genericParameterNeedsInterface.*;
import cocoTest.genericBindingTest.genericParameterNeedsInterface.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  T smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}
