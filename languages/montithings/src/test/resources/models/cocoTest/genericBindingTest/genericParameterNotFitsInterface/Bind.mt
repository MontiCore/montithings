// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNotFitsInterface;

import cocoTest.genericBindingTest.genericParameterNotFitsInterface.*;
import cocoTest.genericBindingTest.genericParameterNotFitsInterface.sensors.*;

component Bind<T1 extends SmokeSensor> {

  /* Subcomponents */
  Intermediate<T1> intermediate;
  T1 smokeSensor;
  Accept a;

  smokeSensor.value -> a.accept;
}