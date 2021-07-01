// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsHandler;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;
import montithings.generator.helper.ASTNoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches for name expressions (here: ports) that are compared to NoData.
 */
public class NoDataComparisionsVisitor
  implements CommonExpressionsVisitor2, CommonExpressionsHandler {

  protected CommonExpressionsTraverser traverser;

  /**
   * All connections during traversing the AST
   */
  protected final List<ASTNameExpression> foundExpressions = new ArrayList<>();

  public void visit(ASTEqualsExpression identityExpression) {

    // Right is name, left is NoData (i.e. "inport == --")
    if (identityExpression.getRight() instanceof ASTNameExpression &&
        identityExpression.getLeft() instanceof ASTNoData) {
      getFoundExpressions().add((ASTNameExpression) identityExpression.getRight());
    }

    // Left is name, right is NoData (i.e. "-- == inport")
    if (identityExpression.getLeft() instanceof ASTNameExpression &&
        identityExpression.getRight() instanceof ASTNoData) {
      getFoundExpressions().add((ASTNameExpression) identityExpression.getLeft());
    }
  }

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4CommonExpressions(this);
    traverser.setCommonExpressionsHandler(this);
    return traverser;
  }

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public List<ASTNameExpression> getFoundExpressions() {
    return foundExpressions;
  }

  @Override public CommonExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(
    CommonExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
