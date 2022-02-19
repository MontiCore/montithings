// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.ocl.oclexpressions._ast.ASTTypeCastExpression;
import de.monticore.ocl.types.check.DeriveSymTypeOfOCLExpressions;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;

public class DeriveSymTypeOfOCLExpressionsForMT extends DeriveSymTypeOfOCLExpressions {
  @Override
  public void traverse(ASTTypeCastExpression node) {
    SymTypeExpression exprResult = null;
    SymTypeExpression typeResult = null;

    //check type of Expression
    if (node.getExpression() != null) {
      node.getExpression().accept(getTraverser());
    }
    if (typeCheckResult.isPresentCurrentResult()) {
      exprResult = typeCheckResult.getCurrentResult();
      typeCheckResult.reset();
    }
    else {
      Log.error(
        "0xA3080 The type of the expression of the OCLTypeCastExpression could not be calculated");
      return;
    }

    //check type of type to cast expression to
    if (node.getMCType() != null) {
      node.getMCType().accept(getTraverser());
    }
    if (typeCheckResult.isPresentCurrentResult()) {
      typeResult = typeCheckResult.getCurrentResult();
      typeCheckResult.reset();
    }
    else {
      Log.error(
        "0xA3081 The type of the MCType of the OCLTypeCastExpression could not be calculated");
      return;
    }

    //check whether typecast is possible
    if (!MontiThingsTypeCheck.castCompatible(typeResult, exprResult)) {
      typeCheckResult.reset();
      Log.error(
        "0xA3082 The type of the expression of the OCLTypeCastExpression can't be cast to given type");
    }
    else {
      //set result to typecasted expression
      typeCheckResult.setCurrentResult(typeResult.deepClone());
    }
  }
}
