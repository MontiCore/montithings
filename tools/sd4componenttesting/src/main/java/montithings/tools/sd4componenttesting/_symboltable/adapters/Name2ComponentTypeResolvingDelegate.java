// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._symboltable.adapters;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IComponentTypeSymbolResolver;
import de.monticore.symboltable.modifiers.AccessModifier;
import montiarc._symboltable.IMontiArcGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

public class Name2ComponentTypeResolvingDelegate implements IComponentTypeSymbolResolver {

  protected IMontiArcGlobalScope globalScope;

  public Name2ComponentTypeResolvingDelegate(@NotNull IMontiArcGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<ComponentTypeSymbol> resolveAdaptedComponentTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<ComponentTypeSymbol> predicate) {
    return globalScope.resolveComponentTypeMany(foundSymbols, name, modifier, predicate);
  }
}
