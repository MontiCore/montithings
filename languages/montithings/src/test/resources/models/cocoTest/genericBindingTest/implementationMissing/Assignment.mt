// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.implementationMissing;

import cocoTest.genericBindingTest.implementationMissing.*;

/* Test component Assignment */
component Assignment {

  /* Subcomponents */
  Bind<SmokeSensorNotExistant<int>,Intermediate<SmokeSensor<int>>> binding;

}
