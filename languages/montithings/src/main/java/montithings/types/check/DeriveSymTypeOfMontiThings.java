// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.types.check.SymTypeExpressionFactory;
import montithings._ast.ASTIsPresentExpression;
import montithings._visitor.MontiThingsHandler;
import montithings._visitor.MontiThingsTraverser;

public class DeriveSymTypeOfMontiThings extends DeriveSymTypeOfExpressionForMT
  implements MontiThingsHandler {

  protected static boolean condition = false;

  protected MontiThingsTraverser traverser;

  @Override public MontiThingsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MontiThingsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTIsPresentExpression node) {
    node.getNameExpression().accept(getTraverser());

    //if used in a condition, the IsPresentExpression evaluates to boolean
    if (condition) {
      typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
  }

  public static void setCondition(boolean b) {
    condition = b;
  }

  public static boolean isCondition() {
    return condition;
  }
}
