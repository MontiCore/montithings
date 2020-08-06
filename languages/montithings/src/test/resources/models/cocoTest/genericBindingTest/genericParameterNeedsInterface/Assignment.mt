// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.genericParameterNeedsInterface;

import cocoTest.genericBindingTest.genericParameterNeedsInterface.*;
import cocoTest.genericBindingTest.genericParameterNeedsInterface.sensors.*;

/* Test component Assignment */
<<deploy>> component Assignment {

  /* Subcomponents */
  Bind<SmokeSensor<int>> binding;

}
