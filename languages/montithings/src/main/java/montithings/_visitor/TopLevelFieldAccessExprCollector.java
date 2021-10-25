package montithings._visitor;

import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;

import java.util.List;
import java.util.ArrayList;

public class TopLevelFieldAccessExprCollector implements CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;
  protected List<ASTFieldAccessExpression> expr = new ArrayList<>();

  @Override
  public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void handle(ASTFieldAccessExpression node) {
    expr.add(node);
  }

  public List<ASTFieldAccessExpression> getExpr() {
    return expr;
  }
}
