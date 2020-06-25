// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.valid;

import cocoTest.genericBindingTest.valid.*;
import cocoTest.genericBindingTest.valid.sensors.*;

/* Test component Assignment */
<<deploy>> component Assignment {

  /* Subcomponents */
  component Bind<SmokeSensor<int>,Intermediate<SmokeSensor<int>>> binding;

}
