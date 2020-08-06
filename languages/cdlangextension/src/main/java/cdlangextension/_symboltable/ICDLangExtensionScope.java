package cdlangextension._symboltable;

import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public  interface ICDLangExtensionScope extends ICDLangExtensionScopeTOP {
  default
  public Optional<CDEImportStatementSymbol> resolveASTCDEImportStatement(String language, CDTypeSymbol symbol) {
    List<CDEImportStatementSymbol> cdeImportStatementSymbols = this.resolveCDEImportStatementMany(language + "." + symbol.getName());
    for (CDEImportStatementSymbol cdeImportStatementSymbol : cdeImportStatementSymbols) {
      if (cdeImportStatementSymbol.isPresentAstNode() && cdeImportStatementSymbol.getAstNode().isPresentNameSymbol() && cdeImportStatementSymbol.getAstNode().getNameSymbol()==symbol) {
        return Optional.of(cdeImportStatementSymbol);
      }
    }
    return Optional.empty();
  }
}
