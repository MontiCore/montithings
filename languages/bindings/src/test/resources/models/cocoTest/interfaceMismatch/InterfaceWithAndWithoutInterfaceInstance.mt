// (c) https://github.com/MontiCore/monticore
package cocoTest.interfaceMismatch;

import cocoTest.interfaceMismatch.ImplementationWithInterfaceComponent;
import cocoTest.interfaceMismatch.InterfaceWithoutInterfaceComponent;

component InterfaceWithAndWithoutInterfaceInstance {
  ImplementationWithInterfaceComponent x;
  InterfaceWithoutInterfaceComponent y;
}