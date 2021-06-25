// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._symboltable.adapters;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.IComponentInstanceSymbolResolver;
import de.monticore.symboltable.modifiers.AccessModifier;
import montiarc._symboltable.IMontiArcGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class Name2ComponentInstanceResolvingDelegate implements IComponentInstanceSymbolResolver {

  protected IMontiArcGlobalScope globalScope;

  public Name2ComponentInstanceResolvingDelegate(@NotNull IMontiArcGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<ComponentInstanceSymbol> resolveAdaptedComponentInstanceSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<ComponentInstanceSymbol> predicate) {
    return globalScope.resolveComponentInstanceMany(foundSymbols, name, modifier, predicate);
  }
}
