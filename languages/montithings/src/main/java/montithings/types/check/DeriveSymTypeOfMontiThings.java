// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import montithings._ast.ASTIsPresentExpression;
import montithings._visitor.MontiThingsVisitor;

public class DeriveSymTypeOfMontiThings extends DeriveSymTypeOfExpression implements MontiThingsVisitor {

  private MontiThingsVisitor realThis;

  @Override
  public void setRealThis(MontiThingsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MontiThingsVisitor getRealThis() {
    return realThis;
  }

  public DeriveSymTypeOfMontiThings() {
    realThis = this;
  }

  @Override
  public void traverse(ASTIsPresentExpression node){
    node.getNameExpression().accept(getRealThis());
  }
}
