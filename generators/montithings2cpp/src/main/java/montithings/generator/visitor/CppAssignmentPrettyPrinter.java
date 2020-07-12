// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.IMontiArcScope;

import java.util.Optional;

import static montithings.generator.visitor.CppPrettyPrinterUtils.capitalize;
import static montithings.generator.visitor.CppPrettyPrinterUtils.isSet;

public class CppAssignmentPrettyPrinter extends AssignmentExpressionsPrettyPrinter {
  public CppAssignmentPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.realThis = this;
  }

  @Override
  public void handle(ASTAssignmentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());

    ASTNameExpression nameExpression;
    if (node.getLeft() instanceof ASTNameExpression) {
      nameExpression = (ASTNameExpression) node.getLeft();
    }
    else {
      super.handle(node);
      return;
    }
    Optional<PortSymbol> port = getPortForName(nameExpression);

    if (port.isPresent()) {
      String prefix;
      if (port.get().isIncoming()) {
        prefix = "input";
      }
      else {
        prefix = "result";
      }
      getPrinter().print(prefix + ".set" + capitalize(nameExpression.getName()) + "( ");

      if (node.getOperator() != ASTConstantsAssignmentExpressions.EQUALS) {
        node.getLeft().accept(getRealThis());
      }

      switch (node.getOperator()) {
        case ASTConstantsAssignmentExpressions.EQUALS:
          break;
        case ASTConstantsAssignmentExpressions.PLUSEQUALS:
          getPrinter().print(("+"));
          break;
        case ASTConstantsAssignmentExpressions.MINUSEQUALS:
          getPrinter().print(("-"));
          break;
        case ASTConstantsAssignmentExpressions.STAREQUALS:
          getPrinter().print(("*"));
          break;
        case ASTConstantsAssignmentExpressions.SLASHEQUALS:
          getPrinter().print(("/"));
          break;
        case ASTConstantsAssignmentExpressions.ANDEQUALS:
          getPrinter().print(("&"));
          break;
        case ASTConstantsAssignmentExpressions.PIPEEQUALS:
          getPrinter().print(("|"));
          break;
        case ASTConstantsAssignmentExpressions.ROOFEQUALS:
          getPrinter().print(("^"));
          break;
        case ASTConstantsAssignmentExpressions.GTGTEQUALS:
          getPrinter().print((">>"));
          break;
        case ASTConstantsAssignmentExpressions.GTGTGTEQUALS:
          getPrinter().print((">>>"));
          break;
        case ASTConstantsAssignmentExpressions.LTLTEQUALS:
          getPrinter().print(("<<"));
          break;
        case ASTConstantsAssignmentExpressions.PERCENTEQUALS:
          getPrinter().print(("%"));
          break;
        default:
          Log.error("0xMT814 Missing implementation for RegularAssignmentExpression");
      }

      node.getRight().accept(getRealThis());
      getPrinter().print(" )");
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
