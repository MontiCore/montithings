/* (c) https://github.com/MontiCore/monticore */
package montithings.generator.visitor;

import de.monticore.mcexpressions._ast.ASTIdentityExpression;
import de.monticore.mcexpressions._ast.ASTNameExpression;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
import montithings._ast.ASTNoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Searches for name expressions (here: ports) that are compared to NoData.
 *
 * @author (last commit) kirchhof
 * @version , 09.02.2020
 * @since
 */
public class NoDataComparisionsVisitor implements MCExpressionsVisitor {

  private MCExpressionsVisitor realThis = this;

  @Override
  public void setRealThis(MCExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCExpressionsVisitor getRealThis() {
    return realThis;
  }

  /**
   * All connections during traversing the AST
   */
  private final List<ASTNameExpression> foundExpressions = new ArrayList<>();

  @Override
  public void visit(ASTIdentityExpression identityExpression) {

    // Right is name, left is NoData (i.e. "inport == --")
    if (identityExpression.getRightExpression() instanceof ASTNameExpression &&
        identityExpression.getLeftExpression() instanceof ASTNoData) {
      getFoundExpressions().add((ASTNameExpression) identityExpression.getRightExpression());
    }

    // Left is name, right is NoData (i.e. "-- == inport")
    if (identityExpression.getLeftExpression() instanceof ASTNameExpression &&
        identityExpression.getRightExpression() instanceof ASTNoData) {
      getFoundExpressions().add((ASTNameExpression) identityExpression.getLeftExpression());
    }
  }

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public List<ASTNameExpression> getFoundExpressions() {
    return foundExpressions;
  }
}
