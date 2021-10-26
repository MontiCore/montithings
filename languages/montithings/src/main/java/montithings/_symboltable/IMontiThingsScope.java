package montithings._symboltable;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/*
  This interface overrides the default resolving behavior to match the
  intended cd4a package naming convention. In future versions, MontiCore
  will handle this out-of-the-box.
 */
public interface IMontiThingsScope extends IMontiThingsScopeTOP {

  /*
   Re-override the filter method from ArcBasis to match the intended cd4a package naming convention.
   */
  @Override
  default Optional<OOTypeSymbol> filterOOType(String name, LinkedListMultimap<String, OOTypeSymbol> symbols) {

    final Set<OOTypeSymbol> resolvedSymbols = new LinkedHashSet<>();

    if (symbols.containsKey(name)) {
      for (de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol symbol : symbols.get(name)) {
        if (symbol.getName().equals(name) || symbol.getFullName().equals(name)) {
          resolvedSymbols.add(symbol);
        }
      }
    }

    return getResolvedOrThrowException(resolvedSymbols);
  }
}
