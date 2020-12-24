// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable.adapters;

import arcbasis._symboltable.IPortSymbolResolver;
import arcbasis._symboltable.PortSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import montithings._symboltable.IMontiThingsGlobalScope;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * ResolvingDelegate that makes resolving of PortSymbols from MontiThings by a qualified name possible.
 *
 * @author Julian Krebber
 */
public class MCQualifiedName2PortResolvingDelegate implements IPortSymbolResolver {

  protected IMontiThingsGlobalScope globalScope;

  public MCQualifiedName2PortResolvingDelegate(@NotNull IMontiThingsGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<PortSymbol> resolveAdaptedPortSymbol(boolean foundSymbols, String name, AccessModifier modifier, Predicate<PortSymbol> predicate) {
    return globalScope.resolvePortMany(foundSymbols, name, modifier, predicate);
  }
}
