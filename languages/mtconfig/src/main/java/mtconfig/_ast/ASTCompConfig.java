// (c) https://github.com/MontiCore/monticore
package mtconfig._ast;

public class ASTCompConfig extends ASTCompConfigTOP {

  /**
   * Adapted method to use the qualified component name for unique component identification.
   */
  @Override
  protected void updateNameSymbolLoader() {
    super.updateNameSymbolLoader();
    String name = this.getSymbol().getFullName();
    if (nameSymbolLoader == null) {
      nameSymbolLoader = new arcbasis._symboltable.ComponentTypeSymbolLoader(name, this.getEnclosingScope());
    }
    else {
      if (!(name.equals(nameSymbolLoader.getName()))) {
        nameSymbolLoader.setName(name);
      }
    }
  }
}
