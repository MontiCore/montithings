// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.implementationMissing;

import cocoTest.genericBindingTest.implementationMissing.*;

/* Test component Assignment */
<<deploy>> component Assignment {

  /* Subcomponents */
  component Bind<SmokeSensorNotExistant<int>,Intermediate<SmokeSensor<int>>> binding;

}
