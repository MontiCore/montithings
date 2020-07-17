package bindings._symboltable.adapters;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IComponentTypeSymbolResolvingDelegate;
import de.monticore.symboltable.modifiers.AccessModifier;
import montithings._symboltable.MontiThingsGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class MCQualifiedName2ComponentTypeResolvingDelegate implements IComponentTypeSymbolResolvingDelegate {

  protected MontiThingsGlobalScope globalScope;

  public MCQualifiedName2ComponentTypeResolvingDelegate(@NotNull MontiThingsGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<ComponentTypeSymbol> resolveAdaptedComponentTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<ComponentTypeSymbol> predicate) {
    return globalScope.resolveComponentTypeMany(foundSymbols, name, modifier, predicate);
  }
}
