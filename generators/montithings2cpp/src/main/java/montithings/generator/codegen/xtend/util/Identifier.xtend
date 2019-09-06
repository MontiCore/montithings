/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 * 
 * http://www.se-rwth.de/
 */
package montithings.generator.codegen.xtend.util

import de.monticore.symboltable.types.JFieldSymbol
import montiarc._symboltable.ComponentInstanceSymbol
import montiarc._symboltable.ComponentSymbol
import montiarc._symboltable.PortSymbol
import montiarc._symboltable.VariableSymbol


class Identifier {

  static Identifier instance;

  def static createInstance(ComponentSymbol comp) {
    instance = new Identifier()
    instance.checkIdentifiers(comp)
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
  def boolean containsIdentifier(String identifier, ComponentSymbol component) {

    for (PortSymbol portSymbol : component.getPorts()) {
      if (portSymbol.getName().equals(identifier)) {
        return true;
      }
    }

    for (JFieldSymbol jFieldSymbol : component.getConfigParameters()) {
      if (jFieldSymbol.getName().equals(identifier)) {
        return true;
      }
    }

    for (VariableSymbol variableSymbol : component.getVariables()) {
      if (variableSymbol.getName().equals(identifier)) {
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

  def private void checkIdentifiers(ComponentSymbol comp) {
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
  def static String getBehaviorImplName() {
    return instance.behaviorImplName;
  }

  /**
   * @return resultName
   */
  def static String getResultName() {
    return instance.resultName;
  }

  /**
   * @return inputName
   */
  def static String getInputName() {
    return instance.inputName;
  }

  /**
   * @return currentStateName
   */
  def static String getCurrentStateName() {
    return instance.currentStateName;
  }
}
