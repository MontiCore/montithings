package cdlangextension._symboltable;

import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.utils.Names;

import java.util.List;
import java.util.Optional;

public  interface ICDLangExtensionScope extends ICDLangExtensionScopeTOP {
  default
  public Optional<CDEImportStatementSymbol> resolveASTCDEImportStatement(String language, CDTypeSymbol symbol) {
    List<CDEImportStatementSymbol> cdeImportStatementSymbols = this.resolveCDEImportStatementMany(Names.getSimpleName(Names.getQualifier(symbol.getFullName()))+"."+language + "." + symbol.getName());
    for (CDEImportStatementSymbol cdeImportStatementSymbol : cdeImportStatementSymbols) {
      if (cdeImportStatementSymbol.isPresentAstNode() && cdeImportStatementSymbol.getAstNode().isPresentNameSymbol() && cdeImportStatementSymbol.getAstNode().getNameSymbol()==symbol) {
        return Optional.of(cdeImportStatementSymbol);
      }
    }
    return Optional.empty();
  }
}
