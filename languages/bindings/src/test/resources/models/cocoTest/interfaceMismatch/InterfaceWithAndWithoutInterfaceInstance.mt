// (c) https://github.com/MontiCore/monticore
package interfaceMismatch;

import interfaceMismatch.ImplementationWithInterfaceComponent;
import interfaceMismatch.InterfaceWithoutInterfaceComponent;

component InterfaceWithAndWithoutInterfaceInstance {
  ImplementationWithInterfaceComponent x;
  InterfaceWithoutInterfaceComponent y;
}