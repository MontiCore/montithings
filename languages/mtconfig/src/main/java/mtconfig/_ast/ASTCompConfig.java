// (c) https://github.com/MontiCore/monticore
package mtconfig._ast;

public class ASTCompConfig extends ASTCompConfigTOP {

  @Override
  protected void updateNameSymbolLoader() {
    super.updateNameSymbolLoader();
    if (nameSymbolLoader == null) {
        nameSymbolLoader = new arcbasis._symboltable.ComponentTypeSymbolLoader(this.getSymbol().getFullName(), this.getEnclosingScope());
    }
    else {
      if (!(this.getSymbol().getFullName()).equals(nameSymbolLoader.getName())) {
        nameSymbolLoader.setName(this.getSymbol().getFullName());
      }
    }
  }
}
