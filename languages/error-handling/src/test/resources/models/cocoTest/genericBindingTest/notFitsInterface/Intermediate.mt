// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.notFitsInterface;

import cocoTest.genericBindingTest.notFitsInterface.*;
import cocoTest.genericBindingTest.notFitsInterface.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
