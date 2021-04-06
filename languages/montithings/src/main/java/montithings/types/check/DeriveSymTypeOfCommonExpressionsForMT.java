// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTConditionalExpression;
import de.monticore.expressions.commonexpressions._ast.ASTInfixExpression;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.types.check.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static de.monticore.ocl.types.check.OCLTypeCheck.*;

public class DeriveSymTypeOfCommonExpressionsForMT extends DeriveSymTypeOfCommonExpressionsWithSIUnitTypes {

  /**
   * All methods in this class are identical to the methods in
   * de.monticore.types.check.DeriveSymTypeOfCommonExpressions.
   * This class is used to ensure that OCLTypeCheck methods are
   * used instead of the normal TypeCheck methods.
   */

  @Override
  protected Optional<SymTypeExpression> calculateConditionalExpressionType(ASTConditionalExpression expr,
      SymTypeExpression conditionResult,
      SymTypeExpression trueResult,
      SymTypeExpression falseResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    //condition has to be boolean
    if (isBoolean(conditionResult)) {
      //check if "then" and "else" are either from the same type or are in sub-supertype relation
      if (compatible(trueResult, falseResult)) {
        wholeResult = Optional.of(trueResult);
      } else if (compatible(falseResult, trueResult)) {
        wholeResult = Optional.of(falseResult);
      } else {
        // first argument can be null since it should not be relevant to the type calculation
        wholeResult = getBinaryNumericPromotion(trueResult, falseResult);
      }
    }
    return wholeResult;
  }

  @Override
  protected Optional<SymTypeExpression> calculateTypeLogical(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    if (isSIUnitType(leftResult) && isSIUnitType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    } else if (isNumericWithSIUnitType(leftResult) && isNumericWithSIUnitType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    return calculateTypeLogicalWithoutSIUnits(expr, leftResult, rightResult);
  }

  protected Optional<SymTypeExpression> calculateTypeLogicalWithoutSIUnits(ASTInfixExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //Option one: they are both numeric types
    if (isNumericType(leftResult) && isNumericType(rightResult)
        || isBoolean(leftResult) && isBoolean(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //Option two: none of them is a primitive type and they are either the same type or in a super/sub type relation
    if (!leftResult.isTypeConstant() && !rightResult.isTypeConstant() &&
        (compatible(leftResult, rightResult) || compatible(rightResult, leftResult))
    ) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    //should never happen, no valid result, error will be handled in traverse
    return Optional.empty();
  }

  /**
   * We use traverse to collect the result of the inner part of the expression and calculate the result for the whole expression
   */
  @Override
  public void traverse(ASTCallExpression expr) {
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    expr.accept(visitor);
    SymTypeExpression innerResult;
    expr.getExpression().accept(getRealThis());
    if (typeCheckResult.isPresentCurrentResult()) {
      innerResult = typeCheckResult.getCurrentResult();
      //resolve methods with name of the inner expression
      List<FunctionSymbol> methodlist = innerResult.getMethodList(expr.getName(), typeCheckResult.isType());
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist,expr);
      //if the last result is static then filter for static methods
      if(typeCheckResult.isType()){
        fittingMethods = filterStaticMethodSymbols(fittingMethods);
      }
      //there can only be one method with the correct arguments and return type
      if (!fittingMethods.isEmpty()) {
        if (fittingMethods.size() > 1) {
          SymTypeExpression returnType = fittingMethods.get(0).getReturnType();
          for (FunctionSymbol method : fittingMethods) {
            if (!returnType.deepEquals(method.getReturnType())) {
              logError("0xA0238", expr.get_SourcePositionStart());
            }
          }
        }
        SymTypeExpression result = fittingMethods.get(0).getReturnType();
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(result);
      } else {
        typeCheckResult.reset();
        logError("0xA0239", expr.get_SourcePositionStart());
      }
    } else {
      Collection<FunctionSymbol> methodcollection = getScope(expr.getEnclosingScope()).resolveFunctionMany(expr.getName());
      List<FunctionSymbol> methodlist = new ArrayList<>(methodcollection);
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist,expr);
      //there can only be one method with the correct arguments and return type
      if (fittingMethods.size() == 1) {
        Optional<SymTypeExpression> wholeResult = Optional.of(fittingMethods.get(0).getReturnType());
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(wholeResult.get());
      } else {
        typeCheckResult.reset();
        logError("0xA0240", expr.get_SourcePositionStart());
      }
    }
  }

  private List<FunctionSymbol> getFittingMethods(List<FunctionSymbol> methodlist, ASTCallExpression expr){
    List<FunctionSymbol> fittingMethods = new ArrayList<>();
    for (FunctionSymbol method : methodlist) {
      //for every method found check if the arguments are correct
      if (expr.getArguments().getExpressionList().size() == method.getParameterList().size()) {
        boolean success = true;
        for (int i = 0; i < method.getParameterList().size(); i++) {
          expr.getArguments().getExpression(i).accept(getRealThis());
          //test if every single argument is correct
          if (!method.getParameterList().get(i).getType().deepEquals(typeCheckResult.getCurrentResult()) &&
              !compatible(method.getParameterList().get(i).getType(), typeCheckResult.getCurrentResult())) {
            success = false;
          }
        }
        if (success) {
          //method has the correct arguments and return type
          fittingMethods.add(method);
        }
      }
    }
    return fittingMethods;
  }

  private boolean isNumericWithSIUnitType(SymTypeExpression type) {
    return type instanceof SymTypeOfNumericWithSIUnit;
  }

}
