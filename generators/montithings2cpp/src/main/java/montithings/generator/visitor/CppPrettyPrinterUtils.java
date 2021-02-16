// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;
import montithings._symboltable.IMontiThingsScope;

import java.util.List;
import java.util.Optional;

/**
 * TODO
 *
 * @since 12.07.20
 */
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

  protected static Optional<PortSymbol> getPortForName(ASTNameExpression node) {
    if (!(node.getEnclosingScope() instanceof IMontiArcScope)) {
      return Optional.empty();
    }
    IMontiArcScope s = (IMontiArcScope) node.getEnclosingScope();
    String name = node.getName();
    return s.resolvePort(name);
  }
}
