// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNotFitsInterface;

import cocoTest.genericBindingTest.genericParameterNotFitsInterface.*;
import cocoTest.genericBindingTest.genericParameterNotFitsInterface.sensors.*;

component Bind<T extends SmokeSensor> {

  /* Subcomponents */
  component Intermediate<T> intermediate;
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
