// (c) https://github.com/MontiCore/monticore
package phyprops._ast;

public class ASTRequirementStatement extends ASTRequirementStatementTOP {
  @Override
  public String getName() {
    return component;
  }

  @Override
  protected void updateComponentSymbolLoader() {
    super.updateComponentSymbolLoader();
    if (componentSymbolLoader == null) {
      if (this.isPresentPackage()) {
        componentSymbolLoader = new arcbasis._symboltable.ComponentTypeSymbolLoader(this.getPackage() + "." + this.getComponent(), this.getEnclosingScope());
      }
    }
    else {
      if (getComponent() != null && isPresentPackage() && !(getPackage() + "." + getComponent()).equals(componentSymbolLoader.getName())) {
        componentSymbolLoader.setName(getPackage() + "." + getComponent());
      }
    }
  }
}
