// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNeedsInterface;

import cocoTest.genericBindingTest.genericParameterNeedsInterface.*;
import cocoTest.genericBindingTest.genericParameterNeedsInterface.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
