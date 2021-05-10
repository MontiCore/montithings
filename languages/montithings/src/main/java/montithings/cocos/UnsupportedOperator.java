// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTExpressionCoCo;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalNotSimilarExpression;
import de.monticore.ocl.optionaloperators._ast.ASTOptionalSimilarExpression;
import de.se_rwth.commons.logging.Log;
import montithings.util.MontiThingsError;

public class UnsupportedOperator implements ExpressionsBasisASTExpressionCoCo {

  @Override public void check(ASTExpression node) {
    if (node instanceof ASTOptionalSimilarExpression) {
      Log.error(String.format(MontiThingsError.UNSUPPORTED_OPERATOR.toString(), "?~~"));
    }
    if (node instanceof ASTOptionalNotSimilarExpression) {
      Log.error(String.format(MontiThingsError.UNSUPPORTED_OPERATOR.toString(), "?!~"));
    }
  }
}
