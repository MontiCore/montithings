// (c) https://github.com/MontiCore/monticore
package cdlangextension._ast;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

/**
 * AST that provides necessary import information for CDTypes.
 */
public class ASTCDEImportStatement extends ASTCDEImportStatementTOP {

  TypeSymbol typeSymbol;

  public TypeSymbol getTypeSymbol() {
    return typeSymbol;
  }

  public void setTypeSymbol(TypeSymbol typeSymbol) {
    this.typeSymbol = typeSymbol;
  }

  @Override public String getName() {
    return getCdType().getPartsList().get(getCdType().getPartsList().size()-1);
  }
}
