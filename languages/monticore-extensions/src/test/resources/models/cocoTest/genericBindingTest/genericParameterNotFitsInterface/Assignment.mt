// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNotFitsInterface;

import cocoTest.genericBindingTest.genericParameterNotFitsInterface.*;
import cocoTest.genericBindingTest.genericParameterNotFitsInterface.sensors.*;

/* Test component Assignment */
<<deploy>> component Assignment {

  /* Subcomponents */
  component Bind<SmokeSensor<int>> binding;

}
