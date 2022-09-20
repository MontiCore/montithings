package montithings.services.prolog_generator.oclquery.generator;

import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

public class CommonExpressionsToPrologPrettyPrinter extends CommonExpressionsPrettyPrinter {

  public CommonExpressionsToPrologPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTModuloExpression node) {
    getPrinter().print("mod(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(", ");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTBooleanAndOpExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(", ");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTBooleanOrOpExpression node) {
    getPrinter().print("(");
    node.getLeft().accept(getTraverser());
    getPrinter().print("; ");
    node.getRight().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTEqualsExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" =:= ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTNotEqualsExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    node.getLeft().accept(getTraverser());
    getPrinter().print(" =\\= ");
    node.getRight().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTConditionalExpression node) {
    getPrinter().print("(");
    node.getCondition().accept(getTraverser());
    getPrinter().print(" -> ");
    node.getTrueExpression().accept(getTraverser());
    getPrinter().print("; ");
    node.getFalseExpression().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTBooleanNotExpression node) {
    getPrinter().print("(\\+" );
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTLogicalNotExpression node) {
    //TODO: LogicalNot vs. BooleanNot in Prolog?
    getPrinter().print("(\\+" );
    node.getExpression().accept(getTraverser());
    getPrinter().print(")");
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    node.getExpression().accept(this.getTraverser());
    this.getPrinter().print("__" + node.getName().toLowerCase());
  }

  @Override
  public void handle(ASTLessEqualExpression node) {
    node.getLeft().accept(this.getTraverser());
    this.getPrinter().print(" =< ");
    node.getRight().accept(this.getTraverser());
  }
}
