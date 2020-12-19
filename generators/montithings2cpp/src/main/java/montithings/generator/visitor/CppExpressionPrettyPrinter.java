// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;
import montithings._symboltable.IMontiThingsScope;
import montithings.generator.helper.ComponentHelper;
import portextensions._ast.ASTSyncStatement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montithings.generator.visitor.CppPrettyPrinterUtils.capitalize;
import static montithings.generator.visitor.CppPrettyPrinterUtils.isSet;

/**
 * Transforms cd attribute calls to getter expression which correspond to the
 * generated C++ class methods
 */
public class CppExpressionPrettyPrinter extends ExpressionsBasisPrettyPrinter {

  public CppExpressionPrettyPrinter(IndentPrinter out) {
    super(out);
    this.realThis = this;
  }

  @Override
  public void handle(ASTNameExpression node) {
    handle(node, false);
  }

  protected void handle(ASTNameExpression node, boolean isComparedToNoData) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    Optional<PortSymbol> port = getPortForName(node);
    if (!port.isPresent()) {
      // Check if this is an Enum Constant from CD4a
      Optional<FieldSymbol> symbol = ((IMontiThingsScope) node.getEnclosingScope()).resolveField(node.getName());
      boolean isEnum = symbol.isPresent() && symbol.get().getAstNode() instanceof ASTCDEnumConstant;

      if (symbol.isPresent() && isEnum) {
        String fullName = symbol.get().getFullName();
        String cppFullyQualifiedName = fullName.replaceAll("\\.", "::");
        getPrinter().print("montithings::" + cppFullyQualifiedName);
      } else {
        getPrinter().print(node.getName());
      }
      return;
    }

    ComponentTypeSymbol comp;
    IMontiArcScope s = (IMontiArcScope) node.getEnclosingScope();
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
        getPrinter().print(prefix + ".get" + capitalize(node.getName()) + "()");
      }
      else {
        getPrinter().print(prefix + ".get" + capitalize(node.getName()) + "().value()");
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
      getPrinter().print(synced.toString());

    }
    else {
      getPrinter().print(node.getName());
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
