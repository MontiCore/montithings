// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolBuilder;
import arcbasis._symboltable.ComponentTypeSymbolLoader;
import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;
import montithings.generator.helper.ASTNoData;
import montithings.generator.helper.ComponentHelper;
import portextensions._ast.ASTSyncStatement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Transforms cd attribute calls to getter expression which correspond to the
 * generated C++ class methods
 */
public class CppExpressionPrettyPrinter extends ExpressionsBasisPrettyPrinter {

  public CppExpressionPrettyPrinter(IndentPrinter out) {
    super(out);
  }

  @Override
  public void handle(ASTNameExpression node) {
    handle(node, false);
  }

  private void handle(ASTNameExpression node, boolean isComparedToNoData) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (!(node.getEnclosingScope() instanceof IMontiArcScope)) {
      printer.print(node.getName());
      return;
    }
    IMontiArcScope s = (IMontiArcScope) node.getEnclosingScope();
    String name = node.getName();
    Optional<PortSymbol> port = s.resolvePort(name);

    ComponentTypeSymbol comp;
    if (s.getSpanningSymbol() instanceof ComponentTypeSymbol) {
      comp = (ComponentTypeSymbol) s.getSpanningSymbol();
    }
    else {
      Log.error("ASTNameExpression " + node.getName()
          + "has an unknown scope (neither statement nor automaton)");
      // throw useless exception to make compiler happy with accessing "comp" afterwards
      throw new IllegalArgumentException();
    }
    List<PortSymbol> portsInBatchStatement = ComponentHelper.getPortsInBatchStatement(comp);
    List<ASTSyncStatement> syncStatements = ComponentHelper
        .elementsOf(comp).filter(ASTSyncStatement.class).toList();

    if (port.isPresent()) {
      String prefix;
      if (port.get().isIncoming()) {
        prefix = "input";
      }
      else {
        prefix = "result";
      }

      if (isComparedToNoData) {
        printer.print(prefix + ".get" + capitalize(node.getName()) + "()");
      }
      else {
        printer.print(prefix + ".get" + capitalize(node.getName()) + "().value()");
      }
    }
    else if (!syncStatements.isEmpty()) {
      StringBuilder synced = new StringBuilder("(");
      for (ASTSyncStatement sync : syncStatements) {
        String s1 = sync
            .getSyncedPortList()
            .stream()
            .map(str -> "input.get" + capitalize(str) + "()" + isSet(portsInBatchStatement, str))
            .collect(Collectors.joining(" && "));
        synced.append(s1);
      }
      synced.append(")");
      printer.print(synced.toString());

    }
    else {
      printer.print(node.getName());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  public void handle(ASTEqualsExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.getLeft() instanceof ASTNameExpression &&
        node.getRight() instanceof ASTNoData) {
      // edge case: we're comparing a name to NoData. Prevent unwrapping optionals
      handle((ASTNameExpression) node.getLeft(), true);
    }
    else {
      node.getLeft().accept(getRealThis());
    }
    getPrinter().print("==");
    if (node.getRight() instanceof ASTNameExpression &&
        node.getLeft() instanceof ASTNoData) {
      // edge case: we're comparing a name to NoData. Prevent unwrapping optionals
      handle((ASTNameExpression) node.getRight(), true);
    }
    else {
      node.getRight().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  private String capitalize(String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  private String isSet(List<PortSymbol> batchPorts, String name) {
    return batchPorts.stream()
        .filter(p -> p.getName().equals(name))
        .findFirst()
        .map(p -> ".size() > 0")
        .orElse("");
  }
}
