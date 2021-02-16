// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.ast.ASTNode;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;

import java.util.List;

public class CppPrettyPrinterUtils {

  protected static String capitalize(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  protected static String isSet(List<PortSymbol> batchPorts, String name) {
    return batchPorts.stream()
      .filter(p -> p.getName().equals(name))
      .findFirst()
      .map(p -> ".size() > 0")
      .orElse("");
  }

  protected static IMontiThingsScope getScopeOfEnclosingComponent(ASTNode node) {
    IMontiThingsScope componentScope = (IMontiThingsScope) node.getEnclosingScope();

    while (!(componentScope.isPresentSpanningSymbol()
      && componentScope.getSpanningSymbol() instanceof ComponentTypeSymbol)) {
      componentScope = componentScope.getEnclosingScope();
      if (componentScope == null) {
        Log.error("ASTNode has an unknown scope (neither statement nor automaton)");
        // throw useless exception to make compiler happy with accessing "comp" afterwards
        throw new IllegalArgumentException();
      }
    }

    return componentScope;
  }
}
