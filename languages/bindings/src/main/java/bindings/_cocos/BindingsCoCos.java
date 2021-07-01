// (c) https://github.com/MontiCore/monticore
package bindings._cocos;

/**
 * Context Conditions for Bindings language
 *
 * @author (last commit) Joshua Fürste
 */
public class BindingsCoCos {
  public static BindingsCoCoChecker createChecker() {
    final BindingsCoCoChecker checker = new BindingsCoCoChecker();

    // Coco 1: Assert that left side is Interface
    checker.addCoCo(new LeftSideIsInterface());
    // Coco 2: Assert that right side is Implementation
    checker.addCoCo(new RightSideIsImplementation());
    // Coco 3: Assert that Implementation has same ports (+ port names) as Interface
    checker.addCoCo(new ImplementationHasSamePortsAsInterface());
    // Coco 4: Assert that Implementation with this specific filename exists
    checker.addCoCo(new ImplementationExists());
    // Coco 5: Assert that Interface with this specific filename exists
    checker.addCoCo(new InterfaceExists());

    return checker;
  }
}
