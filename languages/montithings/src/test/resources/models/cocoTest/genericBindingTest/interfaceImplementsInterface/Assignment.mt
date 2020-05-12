// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceImplementsInterface;

import cocoTest.genericBindingTest.interfaceImplementsInterface.*;
import cocoTest.genericBindingTest.interfaceImplementsInterface.sensors.*;

/* Test component Assignment */
<<deploy>> component Assignment {

  /* Subcomponents */
  component Bind<SmokeSensor<int>,Intermediate<SmokeSensor<int>>> binding;

}
