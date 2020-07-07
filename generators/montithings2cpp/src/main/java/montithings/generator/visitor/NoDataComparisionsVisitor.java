/* (c) https://github.com/MontiCore/monticore */
package montithings.generator.visitor;

import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import montithings.generator.helper.ASTNoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches for name expressions (here: ports) that are compared to NoData.
 */
public class NoDataComparisionsVisitor implements ExpressionsBasisVisitor {

  private ExpressionsBasisVisitor realThis = this;

  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ExpressionsBasisVisitor getRealThis() {
    return realThis;
  }

  /**
   * All connections during traversing the AST
   */
  private final List<ASTNameExpression> foundExpressions = new ArrayList<>();

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

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public List<ASTNameExpression> getFoundExpressions() {
    return foundExpressions;
  }
}
