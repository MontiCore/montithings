// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceImplementsInterface;

import cocoTest.genericBindingTest.interfaceImplementsInterface.*;
import cocoTest.genericBindingTest.interfaceImplementsInterface.sensors.*;

/* Test component Assignment */
component Assignment {

  /* Subcomponents */
   Bind<SmokeSensor<int>,Intermediate<SmokeSensor<int>>> binding;

}
