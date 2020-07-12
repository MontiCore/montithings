// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montithings.generator.helper.ASTNoData;

public class CppCommonExpressionsPrettyPrinter extends CommonExpressionsPrettyPrinter {
  public CppCommonExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.realThis = this;
  }

  @Override public void handle(ASTLessEqualExpression node) {
    handleInfix(node, "<=");
  }

  @Override public void handle(ASTGreaterEqualExpression node) {
    handleInfix(node, ">=");
  }

  @Override public void handle(ASTLessThanExpression node) {
    handleInfix(node, "<");
  }

  @Override public void handle(ASTGreaterThanExpression node) {
    handleInfix(node, ">");
  }

  @Override
  public void handle(ASTEqualsExpression node) {
    handleInfix(node, "==");
  }

  @Override public void handle(ASTNotEqualsExpression node) {
    handleInfix(node, "!=");
  }

  protected void handleInfix(ASTInfixExpression node, String infix) {
    CppExpressionPrettyPrinter expressionPP = new CppExpressionPrettyPrinter(getPrinter());
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    if (node.getLeft() instanceof ASTNameExpression &&
      node.getRight() instanceof ASTNoData) {
      // edge case: we're comparing a name to NoData. Prevent unwrapping optionals
      expressionPP.handle((ASTNameExpression) node.getLeft(), true);
    }
    else {
      node.getLeft().accept(getRealThis());
    }
    getPrinter().print(infix);
    if (node.getRight() instanceof ASTNameExpression &&
      node.getLeft() instanceof ASTNoData) {
      // edge case: we're comparing a name to NoData. Prevent unwrapping optionals
      expressionPP.handle((ASTNameExpression) node.getRight(), true);
    }
    else {
      node.getRight().accept(getRealThis());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }
}
