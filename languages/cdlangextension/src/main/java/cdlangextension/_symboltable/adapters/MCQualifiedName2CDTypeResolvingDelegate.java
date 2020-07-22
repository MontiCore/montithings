package cdlangextension._symboltable.adapters;

import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.ICDTypeSymbolResolvingDelegate;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class MCQualifiedName2CDTypeResolvingDelegate implements ICDTypeSymbolResolvingDelegate {

  protected CD4AnalysisGlobalScope globalScope;

  public MCQualifiedName2CDTypeResolvingDelegate(@NotNull CD4AnalysisGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<CDTypeSymbol> resolveAdaptedCDTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<CDTypeSymbol> predicate) {
    return globalScope.resolveCDTypeMany(foundSymbols, name, modifier, predicate);
  }
}
