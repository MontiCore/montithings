// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceImplementsInterface;

import cocoTest.genericBindingTest.interfaceImplementsInterface.*;
import cocoTest.genericBindingTest.interfaceImplementsInterface.sensors.*;

component Intermediate<T extends SmokeSensorInterface> {

  /* Subcomponents */
  component T smokeSensor;
  component Accept a;

  connect smokeSensor.value -> a.accept;
}
