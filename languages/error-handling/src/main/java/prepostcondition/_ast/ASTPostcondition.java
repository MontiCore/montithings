/* (c) https://github.com/MontiCore/monticore */
package prepostcondition._ast;

import prepostcondition.helper.ExpressionUtil;

public class ASTPostcondition extends ASTPostconditionTOP {

  @Override public String toString() {
    return ExpressionUtil.printExpression(this.getGuard());
  }

}
