// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;

import java.util.ArrayList;
import java.util.List;

public class GuardExpressionVisitor implements ExpressionsBasisVisitor {

  private ExpressionsBasisVisitor realThis = this;

  @Override public ExpressionsBasisVisitor getRealThis() {
    return realThis;
  }

  public void setRealThis(ExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }

  List<ASTNameExpression> expressions = new ArrayList<>();

  @Override
  public void visit(ASTNameExpression node) {
    expressions.add(node);
  }

  public List<ASTNameExpression> getExpressions() {
    return expressions;
  }
}
