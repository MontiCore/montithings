/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json._symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

/**
 * Hand-written extension for modifying the resolveDownMany algorithm for JSON
 * property symbols. Results in a deep-resolve mechanism that finds symbols even
 * without qualified name.
 */
public interface IJSONScope extends IJSONScopeTOP {

  @Override
  default public List<JSONPropertySymbol> resolveJSONPropertyDownMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<JSONPropertySymbol> predicate) {
    if (!isJSONPropertySymbolsAlreadyResolved()) {
      setJSONPropertySymbolsAlreadyResolved(true);
    }
    else {
      return new ArrayList<>();
    }
    
    // 1. Conduct search locally in the current scope
    final List<JSONPropertySymbol> resolved = this.resolveJSONPropertyLocallyMany(foundSymbols, name, modifier, predicate);
    foundSymbols = foundSymbols | resolved.size() > 0;
    
    final String resolveCall = "resolveDownMany(\"" + name + "\", \"" + "JSONPropertySymbol" 
        + "\") in scope \"" + (isPresentName() ? getName() : "") + "\"";
    Log.trace("START " + resolveCall + ". Found #" + resolved.size() + " (local)", "");
    // If no matching symbols have been found...
    if (resolved.isEmpty()) {
      // 2. Continue search in sub scopes and ...
      for (de.monticore.lang.json._symboltable.IJSONScope subScope : getSubScopes()) {
        List<JSONPropertySymbol> resolvedFromSub = new ArrayList<JSONPropertySymbol>();
        // continue with default behavior as long as a qualified name with
        // multiple part is present
        if (getNameParts(name).toList().size() > 1) {
          resolvedFromSub = subScope.continueAsJSONPropertySubScope(foundSymbols, name, modifier, predicate);
        } else {
          // otherwise, perform deep resolving with symbol name over all
          // subscopes recursively
          resolvedFromSub = subScope.resolveJSONPropertyDownMany(foundSymbols, name, modifier, predicate);
        }
        
        foundSymbols = foundSymbols | resolved.size() > 0;
        // 3. unify results
        resolved.addAll(resolvedFromSub);
      }
    }
    Log.trace("END " + resolveCall + ". Found #" + resolved.size(), "");
    setJSONPropertySymbolsAlreadyResolved(false);
    return resolved;
  }
  
}
