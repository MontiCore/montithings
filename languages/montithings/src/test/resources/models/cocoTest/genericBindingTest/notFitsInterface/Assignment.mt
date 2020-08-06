// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.notFitsInterface;

import cocoTest.genericBindingTest.notFitsInterface.*;
import cocoTest.genericBindingTest.notFitsInterface.sensors.*;

/* Test component Assignment */
component Assignment {

  /* Subcomponents */
   Bind<SmokeSensor<int>,Intermediate<SmokeSensor<int>>> binding;

}
