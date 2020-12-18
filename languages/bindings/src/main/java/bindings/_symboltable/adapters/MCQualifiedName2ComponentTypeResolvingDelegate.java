// (c) https://github.com/MontiCore/monticore
package bindings._symboltable.adapters;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.IComponentTypeSymbolResolver;
import de.monticore.symboltable.modifiers.AccessModifier;
import montithings._symboltable.IMontiThingsGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * ResolvingDelegate that makes resolving of ComponentTypeSymbols from MontiThings by a qualified name possible.
 *
 * @author Julian Krebber
 */
public class MCQualifiedName2ComponentTypeResolvingDelegate implements IComponentTypeSymbolResolver {

  protected IMontiThingsGlobalScope globalScope;

  public MCQualifiedName2ComponentTypeResolvingDelegate(@NotNull IMontiThingsGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<ComponentTypeSymbol> resolveAdaptedComponentTypeSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<ComponentTypeSymbol> predicate) {
    return globalScope.resolveComponentTypeMany(foundSymbols, name, modifier, predicate);
  }
}
