// (c) https://github.com/MontiCore/monticore
package mtconfig._ast;

import arcbasis._symboltable.ComponentTypeSymbol;

public class ASTCompConfig extends ASTCompConfigTOP {

  ComponentTypeSymbol componentTypeSymbol;

  public ComponentTypeSymbol getComponentTypeSymbol() {
    return componentTypeSymbol;
  }

  public void setComponentTypeSymbol(ComponentTypeSymbol componentTypeSymbol) {
    this.componentTypeSymbol = componentTypeSymbol;
  }

  @Override public String getName() {
    return getComponentType();
  }
}
