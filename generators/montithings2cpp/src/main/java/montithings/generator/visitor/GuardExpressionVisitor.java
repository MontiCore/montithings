// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisHandler;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;

import java.util.ArrayList;
import java.util.List;

public class GuardExpressionVisitor
  implements ExpressionsBasisVisitor2, ExpressionsBasisHandler {

  protected ExpressionsBasisTraverser traverser;

  List<ASTNameExpression> expressions = new ArrayList<>();

  @Override
  public void visit(ASTNameExpression node) {
    expressions.add(node);
  }

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4ExpressionsBasis(this);
    traverser.setExpressionsBasisHandler(this);
    return traverser;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public List<ASTNameExpression> getExpressions() {
    return expressions;
  }

  @Override public ExpressionsBasisTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(
    ExpressionsBasisTraverser traverser) {
    this.traverser = traverser;
  }
}
