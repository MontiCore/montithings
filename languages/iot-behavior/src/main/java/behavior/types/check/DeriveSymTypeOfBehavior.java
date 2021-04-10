// (c) https://github.com/MontiCore/monticore
package behavior.types.check;

import behavior._ast.ASTAgoQualification;
import behavior._visitor.BehaviorVisitor;
import de.monticore.types.check.DeriveSymTypeOfExpression;

public class DeriveSymTypeOfBehavior extends DeriveSymTypeOfExpression implements BehaviorVisitor {

  private BehaviorVisitor realThis;

  @Override
  public void traverse(ASTAgoQualification node){
    node.getExpression().accept(getRealThis());
  }

  @Override
  public void setRealThis(BehaviorVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public BehaviorVisitor getRealThis() {
    return realThis;
  }
}
