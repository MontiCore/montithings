// (c) https://github.com/MontiCore/monticore
package bindings._ast;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;

/**
 * AST that provides necessary binding information between MontiThings components/instances.
 */
public class ASTBindingRule extends ASTBindingRuleTOP {

  protected  ComponentInstanceSymbol interfaceInstanceSymbol;

  protected  ComponentTypeSymbol interfaceComponentSymbol;

  protected  ComponentTypeSymbol implementationComponentSymbol;

  public ComponentInstanceSymbol getInterfaceInstanceSymbol() {
    return interfaceInstanceSymbol;
  }

  public void setInterfaceInstanceSymbol(
    ComponentInstanceSymbol interfaceInstanceSymbol) {
    this.interfaceInstanceSymbol = interfaceInstanceSymbol;
  }

  public ComponentTypeSymbol getInterfaceComponentSymbol() {
    return interfaceComponentSymbol;
  }

  public void setInterfaceComponentSymbol(
    ComponentTypeSymbol interfaceComponentSymbol) {
    this.interfaceComponentSymbol = interfaceComponentSymbol;
  }

  public ComponentTypeSymbol getImplementationComponentSymbol() {
    return implementationComponentSymbol;
  }

  public void setImplementationComponentSymbol(
    ComponentTypeSymbol implementationComponentSymbol) {
    this.implementationComponentSymbol = implementationComponentSymbol;
  }
}
