package cdlangextension._ast;


public class ASTCDEImportStatement extends ASTCDEImportStatementTOP {

  @Override
  protected void updateNameSymbolLoader() {
    super.updateNameSymbolLoader();
    if (nameSymbolLoader == null) {
      if (this.isPresentPackage()) {
        nameSymbolLoader = new de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolLoader(this.getPackage() + "." + this.getName(), this.getEnclosingScope());
      }
    }
    else {
      if (getName() != null && isPresentPackage() && !(getPackage() + "." + getName()).equals(nameSymbolLoader.getName())) {
        nameSymbolLoader.setName(getPackage() + "." + getName());
      }
    }
  }

}
