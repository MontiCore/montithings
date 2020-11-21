// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;
import montithings._ast.ASTIsPresentExpression;
import montithings._visitor.MontiThingsPrettyPrinter;
import montithings.generator.helper.ComponentHelper;
import portextensions._ast.ASTSyncStatement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montithings.generator.visitor.CppPrettyPrinterUtils.capitalize;
import static montithings.generator.visitor.CppPrettyPrinterUtils.isSet;

/**
 * Handles MontiThings Statements
 */
public class CppMontiThingsPrettyPrinter extends MontiThingsPrettyPrinter {

  @Override
  public void handle(ASTIsPresentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    Optional<PortSymbol> port = getPortForName(node.getNameExpression());
    if (!port.isPresent()) {
      getRealThis().handle(node.getNameExpression());
      return;
    }

    ComponentTypeSymbol comp;
    IMontiArcScope s = (IMontiArcScope) node.getEnclosingScope();
    if (s.getSpanningSymbol() instanceof ComponentTypeSymbol) {
      comp = (ComponentTypeSymbol) s.getSpanningSymbol();
    }
    else {
      Log.error("ASTNameExpression " + node.getNameExpression().getName()
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

      getPrinter().print(prefix + ".get" + capitalize(node.getNameExpression().getName()) + "()");

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
      getPrinter().print(synced.toString());

    }
    else {
      getPrinter().print(node.getNameExpression().getName());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  protected Optional<PortSymbol> getPortForName(ASTNameExpression node) {
    if (!(node.getEnclosingScope() instanceof IMontiArcScope)) {
      getPrinter().print(node.getName());
      return Optional.empty();
    }
    IMontiArcScope s = (IMontiArcScope) node.getEnclosingScope();
    String name = node.getName();
    return s.resolvePort(name);
  }
}
