// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.types.check.SymTypeExpressionFactory;
import montithings._ast.ASTIsPresentExpression;
import montithings._visitor.MontiThingsVisitor;

public class DeriveSymTypeOfMontiThings extends DeriveSymTypeOfExpressionForMT
  implements MontiThingsVisitor {

  private static boolean condition = false;

  private MontiThingsVisitor realThis;

  public DeriveSymTypeOfMontiThings() {
    realThis = this;
  }

  @Override
  public MontiThingsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MontiThingsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void traverse(ASTIsPresentExpression node) {
    node.getNameExpression().accept(getRealThis());

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
