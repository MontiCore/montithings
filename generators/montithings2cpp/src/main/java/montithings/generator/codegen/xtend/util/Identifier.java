// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend.util;

import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.types.typesymbols._symboltable.TypeVarSymbol;


class Identifier {

  static Identifier instance;

  static void createInstance(ComponentTypeSymbol comp) {
    instance = new Identifier();
    instance.checkIdentifiers(comp);
  }

  String resultName = "result";

  String inputName = "input";

  String behaviorImplName = "behaviorImpl";

  String currentStateName = "currentState";

  /**
   * Checks whether component parameter, variable, subcomponent instance, or
   * port names contain the identifier given as the parameter.
   * 
   * @param identifier The name to check
   * @return True, iff. there is at least one identifier that is equal to the
   * given identifier
   */
  boolean containsIdentifier(String identifier, ComponentTypeSymbol component) {

    for (PortSymbol portSymbol : component.getPorts()) {
      if (portSymbol.getName().equals(identifier)) {
        return true;
      }
    }

    for (FieldSymbol fieldSymbol : component.getParameters()) {
      if (fieldSymbol.getName().equals(identifier)) {
        return true;
      }
    }

    for (TypeVarSymbol typeVarSymbol : component.getTypeParameters()) {
      if (typeVarSymbol.getName().equals(identifier)) {
        return true;
      }
    }

    for (ComponentInstanceSymbol instanceSymbol : component.getSubComponents()) {
      if (instanceSymbol.getName().equals(identifier)) {
        return true;
      }
    }

    return false;
  }

  private void checkIdentifiers(ComponentTypeSymbol comp) {
    if (containsIdentifier("result", comp)) {
      resultName = "r__result";
    }
    if (this.containsIdentifier("input", comp)) {
      inputName = "r__input";
    }
    if (this.containsIdentifier("behaviorImpl", comp)) {
      behaviorImplName = "r__behaviorImpl";
    }

    if (this.containsIdentifier("currentState", comp)) {
      currentStateName = "r__currentState";
    }
  }

  /**
   * @return behaviorImplName
   */
  static String getBehaviorImplName() {
    return instance.behaviorImplName;
  }

  /**
   * @return resultName
   */
  static String getResultName() {
    return instance.resultName;
  }

  /**
   * @return inputName
   */
  static String getInputName() {
    return instance.inputName;
  }

  /**
   * @return currentStateName
   */
  static String getCurrentStateName() {
    return instance.currentStateName;
  }
}
