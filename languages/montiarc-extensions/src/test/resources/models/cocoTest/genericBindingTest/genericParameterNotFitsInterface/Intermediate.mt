// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNotFitsInterface;

import cocoTest.genericBindingTest.genericParameterNotFitsInterface.*;
import cocoTest.genericBindingTest.genericParameterNotFitsInterface.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
