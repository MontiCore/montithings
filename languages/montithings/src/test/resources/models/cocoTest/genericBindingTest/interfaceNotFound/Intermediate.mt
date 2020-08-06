// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceNotFound;

import cocoTest.genericBindingTest.interfaceNotFound.*;
import cocoTest.genericBindingTest.interfaceNotFound.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
   T smokeSensor;
   Accept a;

   smokeSensor.value -> a.accept;
}
