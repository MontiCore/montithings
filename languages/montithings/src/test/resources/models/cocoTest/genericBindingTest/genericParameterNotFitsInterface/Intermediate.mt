// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNotFitsInterface;

import cocoTest.genericBindingTest.genericParameterNotFitsInterface.*;
import cocoTest.genericBindingTest.genericParameterNotFitsInterface.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  T smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}
