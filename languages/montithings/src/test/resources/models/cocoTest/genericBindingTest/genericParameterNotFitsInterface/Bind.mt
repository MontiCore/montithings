// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNotFitsInterface;

import cocoTest.genericBindingTest.genericParameterNotFitsInterface.*;
import cocoTest.genericBindingTest.genericParameterNotFitsInterface.sensors.*;

component Bind<T extends SmokeSensor> {

  /* Subcomponents */
  Intermediate<T> intermediate;
  T smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}
