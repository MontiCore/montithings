// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montithings._visitor.MontiThingsVisitor;

import java.util.ArrayList;
import java.util.List;

public class GuardExpressionVisitor implements MontiThingsVisitor {

  private MontiThingsVisitor realThis = this;

  @Override public MontiThingsVisitor getRealThis() {
    return realThis;
  }

  public void setRealThis(MontiThingsVisitor realThis) {
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
