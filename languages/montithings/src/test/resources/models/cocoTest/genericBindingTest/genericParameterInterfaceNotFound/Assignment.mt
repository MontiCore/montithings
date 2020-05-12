// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterInterfaceNotFound;

import cocoTest.genericBindingTest.genericParameterInterfaceNotFound.*;
import cocoTest.genericBindingTest.genericParameterInterfaceNotFound.sensors.*;

/* Test component Assignment */
<<deploy>> component Assignment {

  /* Subcomponents */
  component Bind<SmokeSensor<int>> binding;

}
