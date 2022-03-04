// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.helper.ComponentHelper;

import java.util.List;
import java.util.Optional;

import static montithings.util.IdentifierUtils.getPortForName;

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

  protected static boolean usesSiUnit(PortSymbol port) {
    return port.getType() instanceof SymTypeOfNumericWithSIUnit;
  }

  protected static boolean usesSiUnit(SymTypeExpression expr) {
    return expr instanceof SymTypeOfNumericWithSIUnit;
  }

  protected static boolean assignmentUsesSiUnits(ASTAssignmentExpression node, TypeCheck tc) {
    return usesSiUnit(tc.typeOf(node.getLeft())) && usesSiUnit(tc.typeOf(node.getRight()));
  }

  protected static boolean isPort(ASTExpression node) {
    ASTNameExpression nameExpr = (ASTNameExpression) node;
    return getPortForName(nameExpr).isPresent();
  }

  protected static boolean isStateVariable(ASTExpression node) {
    IMontiThingsScope componentScope = getScopeOfEnclosingComponent(node);
    ComponentTypeSymbol component = (ComponentTypeSymbol) componentScope.getSpanningSymbol();

    List<VariableSymbol> stateVariables = ComponentHelper.getVariablesAndParameters(component);
    ASTNameExpression nameExpr = (ASTNameExpression) node;
    IMontiThingsScope scope = (IMontiThingsScope) node.getEnclosingScope();
    Optional<VariableSymbol> foundVariable = scope.resolveVariable(nameExpr.getName());
    return foundVariable.filter(stateVariables::contains).isPresent();
  }
}
