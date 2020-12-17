// (c) https://github.com/MontiCore/monticore
package mtconfig._ast;

public class ASTPortTemplateTag extends ASTPortTemplateTagTOP {

  /**
   * Adapted method to use the qualified port name for unique port identification.
   */
  @Override
  protected void updateNameSymbolLoader() {
    super.updateNameSymbolLoader();
    if (nameSymbolLoader == null) {
      nameSymbolLoader = new arcbasis._symboltable.PortSymbolLoader(this.getSymbol().getFullName(), this.getEnclosingScope());
    }
    else {
      if (!(this.getSymbol().getFullName()).equals(nameSymbolLoader.getName())) {
        nameSymbolLoader.setName(this.getSymbol().getFullName());
      }
    }
  }
}