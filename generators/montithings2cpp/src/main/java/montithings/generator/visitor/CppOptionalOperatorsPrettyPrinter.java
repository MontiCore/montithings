// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.commonexpressions._ast.ASTInfixExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.ocl.optionaloperators._ast.*;
import de.monticore.ocl.optionaloperators.prettyprint.OptionalOperatorsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montithings.generator.codegen.util.Identifier;

import java.util.Optional;

import static montithings.generator.visitor.CppPrettyPrinterUtils.capitalize;
import static montithings.generator.visitor.CppPrettyPrinterUtils.getPortForName;

public class CppOptionalOperatorsPrettyPrinter extends OptionalOperatorsPrettyPrinter {
  public CppOptionalOperatorsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.realThis = this;
  }

  @Override public void handle(ASTOptionalExpressionPrefix node) {
    handle(node, "?", ":");
  }

  @Override public void handle(ASTOptionalLessEqualExpression node) {
    handle(node, "&&", "<=");
  }

  @Override public void handle(ASTOptionalGreaterEqualExpression node) {
    handle(node, "&&", ">=");
  }

  @Override public void handle(ASTOptionalLessThanExpression node) {
    handle(node, "&&", "<");
  }

  @Override public void handle(ASTOptionalGreaterThanExpression node) {
    handle(node, "&&", ">");
  }

  @Override public void handle(ASTOptionalEqualsExpression node) {
    handle(node, "&&", "==");
  }

  @Override public void handle(ASTOptionalNotEqualsExpression node) {
    handle(node, "&&", "!=");
  }

  protected void handle(ASTInfixExpression node, String leftOperator, String rightOperator) {
    if (!handlePort(node, leftOperator, rightOperator)) {
      CommentPrettyPrinter.printPreComments(node, getPrinter());
      node.getLeft().accept(getRealThis());
      getPrinter().print(".has_value ()");
      getPrinter().print(" " + leftOperator + " ");
      node.getLeft().accept(getRealThis());
      getPrinter().print(".value ()");
      getPrinter().print(" " + rightOperator + " ");
      node.getRight().accept(getRealThis());
      CommentPrettyPrinter.printPostComments(node, getPrinter());
    }
  }

  /**
   * Trys if leftOperator is a port. If so prints prints optional operator with correct prefix
   * @param node the optional operator ast node
   * @param leftOperator the operator to place between the isPresent() check and the get() call
   * @param rightOperator the operator to place between the get() call and the right node
   * @return true iff this operator was handled by this method, false if getLeft is no port
   */
  protected boolean handlePort(ASTInfixExpression node, String leftOperator, String rightOperator) {
    if (!(node.getLeft() instanceof ASTNameExpression)) {
      return false;
    }

    ASTNameExpression nameExpr = (ASTNameExpression) node.getLeft();

    Optional<PortSymbol> port = getPortForName(nameExpr);
    if (!port.isPresent()) {
      return false;
    }

    if (port.isPresent()) {
      String prefix;
      if (port.get().isIncoming()) {
        prefix = Identifier.getInputName();
      }
      else {
        prefix = Identifier.getResultName();
      }

      CommentPrettyPrinter.printPreComments(node, getPrinter());
      getPrinter().print(prefix + ".get" + capitalize(nameExpr.getName()) + "().has_value ()");
      getPrinter().print(" " + leftOperator + " ");
      getPrinter().print(prefix + ".get" + capitalize(nameExpr.getName()) + "().value ()");
      getPrinter().print(" " + rightOperator + " ");
      node.getRight().accept(getRealThis());
      CommentPrettyPrinter.printPostComments(node, getPrinter());
      return true;
    }
    else {
      return false;
    }
  }
}
