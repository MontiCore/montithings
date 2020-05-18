// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceNotFound;

import cocoTest.genericBindingTest.interfaceNotFound.*;
import cocoTest.genericBindingTest.interfaceNotFound.sensors.*;

/* Test component Assignment */
<<deploy>> component Assignment {

  /* Subcomponents */
  component Bind<SmokeSensor<int>,Intermediate<SmokeSensor<int>>> binding;

}
