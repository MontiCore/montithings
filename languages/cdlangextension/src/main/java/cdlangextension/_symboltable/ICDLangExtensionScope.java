// (c) https://github.com/MontiCore/monticore
package cdlangextension._symboltable;

import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.utils.Names;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public  interface ICDLangExtensionScope extends ICDLangExtensionScopeTOP {

  default
  public Optional<CDEImportStatementSymbol> resolveASTCDEImportStatement(String language, OOTypeSymbol symbol) {
    List<CDEImportStatementSymbol> cdeImportStatementSymbols = this.resolveCDEImportStatementMany(Names.getSimpleName(Names.getQualifier(symbol.getFullName()))+"."+language + "." + Names.getSimpleName(symbol.getName()));
    for (CDEImportStatementSymbol cdeImportStatementSymbol : cdeImportStatementSymbols) {
      if (cdeImportStatementSymbol.isPresentAstNode()) {
        return Optional.of(cdeImportStatementSymbol);
      }
    }
    return Optional.empty();
  }

  /**
   * NOTE: According to SVa, there is a CD4Analysis bug that causes types to be resolved
   * twice. This is his proposed fix. Remove once CD4A works as expected.
   */
  @Override default <T extends ISymbol> Optional<T> getResolvedOrThrowException(
    Collection<T> resolved) {
    return ICDLangExtensionScopeTOP.super.getResolvedOrThrowException(
      resolved.stream().distinct().collect(Collectors.toList())
    );
  }
}
