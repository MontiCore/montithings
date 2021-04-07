// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.types.check.DeriveSymTypeOfAssignmentExpressionsWithSIUnitTypes;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;

import java.util.Optional;

import static de.monticore.ocl.types.check.OCLTypeCheck.*;

public class DeriveSymTypeOfAssignmentExpressionsForMT extends DeriveSymTypeOfAssignmentExpressionsWithSIUnitTypes {
  /**
   * All methods in this class are identical to the methods in
   * de.monticore.types.check.DeriveSymTypeOfCommonExpressions.
   * This class is used to ensure that OCLTypeCheck methods are
   * used instead of the normal TypeCheck methods.
   */

  @Override
  protected Optional<SymTypeExpression> calculateRegularAssignment(ASTAssignmentExpression expr, SymTypeExpression leftResult, SymTypeExpression rightResult) {
    //option one: both are numeric types and are assignable
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    if (isNumericType(leftResult) && isNumericType(rightResult) && compatible(leftResult, rightResult)) {
      wholeResult = Optional.of(SymTypeExpressionFactory.createTypeConstant(leftResult.print()));
    } else if (compatible(leftResult, rightResult)) {
      //option two: none of them are primitive types and they are either from the same class or stand in a super/subtype relation with the supertype on the left side
      wholeResult = Optional.of(leftResult);
    }
    return wholeResult;
  }
}
