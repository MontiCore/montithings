// (c) https://github.com/MontiCore/monticore
package cdlangextension._ast;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;

/**
 * AST that provides necessary import information for CDTypes.
 */
public class ASTCDEImportStatement extends ASTCDEImportStatementTOP {

  CDTypeSymbol cdTypeSymbol;

  public CDTypeSymbol getCdTypeSymbol() {
    return cdTypeSymbol;
  }

  public void setCdTypeSymbol(CDTypeSymbol cdTypeSymbol) {
    this.cdTypeSymbol = cdTypeSymbol;
  }

  @Override public String getName() {
    return getCdType().getPartsList().get(getCdType().getPartsList().size()-1);
  }
}
