// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._symboltable.adapters;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IComponentTypeSymbolResolver;
import de.monticore.symboltable.modifiers.AccessModifier;
import montiarc._symboltable.IMontiArcGlobalScope;
import montiarc._symboltable.IMontiArcScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class MCQualifiedName2ComponentTypeResolvingDelegate implements IComponentTypeSymbolResolver {

  protected IMontiArcGlobalScope globalScope;

  public MCQualifiedName2ComponentTypeResolvingDelegate(@NotNull IMontiArcGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<ComponentTypeSymbol> resolveAdaptedComponentTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<ComponentTypeSymbol> predicate) {
    for (IMontiArcScope scope: globalScope.getSubScopes()) {
      if (scope.getName().equals(name)) {
        return globalScope.resolveComponentTypeMany(foundSymbols,name, modifier, predicate);
      }
    }

    if (globalScope.isFileLoaded(name)) {
      return globalScope.resolveComponentTypeMany(foundSymbols, name, modifier, predicate);
    }
    return globalScope.resolveComponentTypeMany(foundSymbols, name, modifier, predicate);
  }
}
