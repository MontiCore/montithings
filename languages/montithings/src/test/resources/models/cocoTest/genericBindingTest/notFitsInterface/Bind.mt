// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.notFitsInterface;

import cocoTest.genericBindingTest.notFitsInterface.*;
import cocoTest.genericBindingTest.notFitsInterface.sensors.*;

component Bind<T extends SmokeSensorInterface, T2 extends IntermediateInterface> {

  /* Subcomponents */
   T2 intermediate;
   T smokeSensor;
   Accept a;

   smokeSensor.value -> a.accept;
}
