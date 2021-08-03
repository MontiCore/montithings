// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.helper.ComponentHelper;

import java.util.List;
import java.util.Optional;

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

  protected static boolean isStateVariable(ASTExpression node) {
    IMontiThingsScope componentScope = getScopeOfEnclosingComponent(node);
    ComponentTypeSymbol component = (ComponentTypeSymbol) componentScope.getSpanningSymbol();

    List<VariableSymbol> stateVariables = ComponentHelper.getVariablesAndParameters(component);
    ASTNameExpression nameExpr = (ASTNameExpression) node;
    IMontiThingsScope scope = (IMontiThingsScope) node.getEnclosingScope();
    Optional<VariableSymbol> foundVariable = scope.resolveVariable(nameExpr.getName());
    if (foundVariable.isPresent()) {
      return stateVariables.contains(foundVariable.get());
    }
    return false;
  }
}
