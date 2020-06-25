// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.notFitsInterface;

import cocoTest.genericBindingTest.notFitsInterface.*;
import cocoTest.genericBindingTest.notFitsInterface.sensors.*;

component Bind<T extends SmokeSensorInterface, T2 extends IntermediateInterface> {

  /* Subcomponents */
  component T2 intermediate;
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
