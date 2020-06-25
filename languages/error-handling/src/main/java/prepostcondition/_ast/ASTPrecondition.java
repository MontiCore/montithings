/* (c) https://github.com/MontiCore/monticore */
package prepostcondition._ast;

import prepostcondition.helper.ExpressionUtil;

public class ASTPrecondition extends ASTPreconditionTOP {

  @Override public String toString() {
    return ExpressionUtil.printExpression(this.getGuard());
  }
}
