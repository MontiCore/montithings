// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.util;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;


public class Identifier {

  static Identifier instance;

  public static void createInstance(ComponentTypeSymbol comp) {
    instance = new Identifier();
    instance.checkIdentifiers(comp);
  }

  String resultName = "result";

  String inputName = "input";

  String behaviorImplName = "behaviorImpl";

  String stateName = "state";

  String interfaceName = "interface";

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

    for (VariableSymbol fieldSymbol : component.getParameters()) {
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

  protected void checkIdentifiers(ComponentTypeSymbol comp) {
    if (containsIdentifier("result", comp)) {
      resultName = "r__result";
    }
    if (this.containsIdentifier("input", comp)) {
      inputName = "r__input";
    }
    if (this.containsIdentifier("behaviorImpl", comp)) {
      behaviorImplName = "r__behaviorImpl";
    }
    if (this.containsIdentifier("state", comp)) {
      stateName = "r__state";
    }
    if (this.containsIdentifier("interface", comp)) {
      interfaceName = "r__interface";
    }
  }

  public static String getResultName() {
    return instance.resultName;
  }

  public static String getBehaviorImplName() {
    return instance.behaviorImplName;
  }

  public static String getInputName() {
    return instance.inputName;
  }

  public static String getStateName() {
    return instance.stateName;
  }

  public static String getInterfaceName() {
    return instance.interfaceName;
  }
}
