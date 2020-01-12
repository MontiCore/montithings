// (c) https://github.com/MontiCore/monticore
package bindings.cocos;

import bindings._cocos.BindingsCoCoChecker;

/**
 * Context Conditions for MontiThings language
 *
 * @author (last commit) Joshua Fürste
 */
public class BindingsCoCos {
  public static BindingsCoCoChecker createChecker() {
    final BindingsCoCoChecker checker = new BindingsCoCoChecker();
    return checker
        // Coco 1: Assert that left side is Interface
        .addCoCo(new LeftSideIsInterface())
        // Coco 2: Assert that right side is Implementation
        .addCoCo(new RightSideIsImplementation())
        // Coco 3: Assert that Implementation has same ports (+ port names) as Interface
        .addCoCo(new ImplementationHasSamePortsAsInterface())
        // Coco 4: Assert that Implementation with this specific filename exists
        .addCoCo(new ImplementationExists())
        // Coco 5: Assert that Interface with this specific filename exists
        .addCoCo(new InterfaceExists())
        ;
  }
}
