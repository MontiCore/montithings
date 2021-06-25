// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._symboltable.adapters;

import arcbasis._symboltable.IPortSymbolResolver;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import montiarc._symboltable.IMontiArcGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class Name2PortResolvingDelegate implements IPortSymbolResolver {

  protected IMontiArcGlobalScope globalScope;

  public Name2PortResolvingDelegate(@NotNull IMontiArcGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<PortSymbol> resolveAdaptedPortSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<PortSymbol> predicate) {
    return globalScope.resolvePortMany(foundSymbols, name, modifier, predicate);
  }
}
