// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.valid;

import cocoTest.genericBindingTest.valid.*;
import cocoTest.genericBindingTest.valid.sensors.*;

/* Test component Assignment */
component Assignment {

  /* Subcomponents */
  Bind<SmokeSensor<int>,Intermediate<SmokeSensor<int>>> binding;
  //Bind binding;
}
