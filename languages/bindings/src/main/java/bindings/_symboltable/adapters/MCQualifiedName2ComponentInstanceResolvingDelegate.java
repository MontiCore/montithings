package bindings._symboltable.adapters;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.IComponentInstanceSymbolResolvingDelegate;
import de.monticore.symboltable.modifiers.AccessModifier;
import montithings._symboltable.MontiThingsGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class MCQualifiedName2ComponentInstanceResolvingDelegate implements IComponentInstanceSymbolResolvingDelegate {

  protected MontiThingsGlobalScope globalScope;

  public MCQualifiedName2ComponentInstanceResolvingDelegate(@NotNull MontiThingsGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<ComponentInstanceSymbol> resolveAdaptedComponentInstanceSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<ComponentInstanceSymbol> predicate) {
    return globalScope.resolveComponentInstanceMany(foundSymbols, name, modifier, predicate);
  }
}
