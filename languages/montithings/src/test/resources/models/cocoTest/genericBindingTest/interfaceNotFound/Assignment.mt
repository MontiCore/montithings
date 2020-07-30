// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceNotFound;

import cocoTest.genericBindingTest.interfaceNotFound.*;
import cocoTest.genericBindingTest.interfaceNotFound.sensors.*;

/* Test component Assignment */
 component Assignment {

  /* Subcomponents */
   Bind<SmokeSensor<int>,Intermediate<SmokeSensor<int>>> binding;

}
