// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNeedsInterface;

import cocoTest.genericBindingTest.genericParameterNeedsInterface.*;
import cocoTest.genericBindingTest.genericParameterNeedsInterface.sensors.*;

/* Test component Assignment */
<<deploy>> component Assignment {

  /* Subcomponents */
  component Bind<SmokeSensor<int>> binding;

}
