// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import com.google.common.collect.Maps;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import montithings.MontiThingsMill;
import org.assertj.core.util.Lists;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.ocl.types.check.OCLTypeCheck.compatible;
import static de.monticore.ocl.types.check.OCLTypeCheck.isBoolean;
import static de.monticore.types.check.TypeCheck.isString;

public class DeriveSymTypeOfCommonExpressionsForMT
  extends DeriveSymTypeOfCommonExpressionsWithSIUnitTypes {

  /**
   * All methods in this class are alomst identical to the methods in
   * de.monticore.types.check.DeriveSymTypeOfCommonExpressions.
   * This class is used to ensure that OCLTypeCheck methods are used
   * used instead of the normal TypeCheck methods and that the condition
   * flag is properly set when working with IsPresentExpressions.
   */

  @Override
  protected Optional<SymTypeExpression> calculateConditionalExpressionType(
    ASTConditionalExpression expr,
    SymTypeExpression conditionResult,
    SymTypeExpression trueResult,
    SymTypeExpression falseResult) {
    Optional<SymTypeExpression> wholeResult = Optional.empty();
    //condition has to be boolean
    if (isBoolean(conditionResult)) {
      //check if "then" and "else" are either from the same type or are in sub-supertype relation
      if (compatible(trueResult, falseResult)) {
        wholeResult = Optional.of(trueResult);
      }
      else if (compatible(falseResult, trueResult)) {
        wholeResult = Optional.of(falseResult);
      }
      else {
        // first argument can be null since it should not be relevant to the type calculation
        wholeResult = getBinaryNumericPromotion(trueResult, falseResult);
      }
    }
    return wholeResult;
  }

  @Override
  protected Optional<SymTypeExpression> calculateEqualsExpression(ASTEqualsExpression expr) {
    return calculateTypeLogical(expr, expr.getRight(), expr.getLeft());
  }

  @Override
  protected Optional<SymTypeExpression> calculateNotEqualsExpression(ASTNotEqualsExpression expr) {
    return calculateTypeLogical(expr, expr.getRight(), expr.getLeft());
  }

  @Override
  protected Optional<SymTypeExpression> calculateLessEqualExpression(ASTLessEqualExpression expr) {
    return calculateTypeCompare(expr, expr.getRight(), expr.getLeft());
  }

  @Override
  protected Optional<SymTypeExpression> calculateGreaterEqualExpression(
    ASTGreaterEqualExpression expr) {
    return calculateTypeCompare(expr, expr.getRight(), expr.getLeft());
  }

  @Override
  protected Optional<SymTypeExpression> calculateLessThanExpression(ASTLessThanExpression expr) {
    return calculateTypeCompare(expr, expr.getRight(), expr.getLeft());
  }

  @Override
  protected Optional<SymTypeExpression> calculateGreaterThanExpression(
    ASTGreaterThanExpression expr) {
    return calculateTypeCompare(expr, expr.getRight(), expr.getLeft());
  }

  public Optional<SymTypeExpression> calculateTypeCompare(ASTInfixExpression expr,
    ASTExpression right, ASTExpression left) {
    boolean b = DeriveSymTypeOfMontiThings.isCondition();
    DeriveSymTypeOfMontiThings.setCondition(false);
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0241");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right,
      "0xA0242");
    DeriveSymTypeOfMontiThings.setCondition(b);
    return calculateTypeCompare(expr, rightResult, leftResult);
  }

  public Optional<SymTypeExpression> calculateTypeLogical(ASTInfixExpression expr,
    ASTExpression right, ASTExpression left) {
    boolean b = DeriveSymTypeOfMontiThings.isCondition();
    DeriveSymTypeOfMontiThings.setCondition(false);
    SymTypeExpression leftResult = acceptThisAndReturnSymTypeExpressionOrLogError(left, "0xA0244");
    SymTypeExpression rightResult = acceptThisAndReturnSymTypeExpressionOrLogError(right,
      "0xA0245");
    DeriveSymTypeOfMontiThings.setCondition(b);
    return calculateTypeLogical(expr, rightResult, leftResult);
  }

  @Override
  protected Optional<SymTypeExpression> calculateTypeLogical(ASTInfixExpression expr,
    SymTypeExpression rightResult, SymTypeExpression leftResult) {
    if (isSIUnitType(leftResult) && isSIUnitType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    else if (isNumericWithSIUnitType(leftResult) && isNumericWithSIUnitType(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeConstant("boolean"));
    }
    return calculateTypeLogicalWithoutSIUnits(expr, leftResult, rightResult);
  }

  protected Optional<SymTypeExpression> calculateTypeLogicalWithoutSIUnits(ASTInfixExpression expr,
    SymTypeExpression rightResult, SymTypeExpression leftResult) {
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
    CommonExpressionsTraverser traverser = CommonExpressionsMill.traverser();
    traverser.setCommonExpressionsHandler(visitor);
    traverser.add4CommonExpressions(visitor);
    traverser.setExpressionsBasisHandler(visitor);
    traverser.add4ExpressionsBasis(visitor);
    expr.accept(traverser);

    SymTypeExpression innerResult;
    expr.getExpression().accept(getTraverser());
    if (typeCheckResult.isPresentCurrentResult()) {
      innerResult = typeCheckResult.getCurrentResult();
      //resolve methods with name of the inner expression
      List<FunctionSymbol> methodlist = innerResult
        .getMethodList(expr.getName(), typeCheckResult.isType());
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist, expr);
      //if the last result is static then filter for static methods
      if (typeCheckResult.isType()) {
        fittingMethods = filterModifiersFunctions(fittingMethods);
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
      }
      else {
        typeCheckResult.reset();
        logError("0xA0239", expr.get_SourcePositionStart());
      }
    }
    else {
      Collection<FunctionSymbol> methodcollection = getScope(expr.getEnclosingScope())
        .resolveFunctionMany(expr.getName());
      List<FunctionSymbol> methodlist = new ArrayList<>(methodcollection);
      //count how many methods can be found with the correct arguments and return type
      List<FunctionSymbol> fittingMethods = getFittingMethods(methodlist, expr);
      //there can only be one method with the correct arguments and return type
      if (fittingMethods.size() == 1) {
        Optional<SymTypeExpression> wholeResult = Optional
          .of(fittingMethods.get(0).getReturnType());
        typeCheckResult.setMethod();
        typeCheckResult.setCurrentResult(wholeResult.get());
      }
      else {
        typeCheckResult.reset();
        logError("0xA0240", expr.get_SourcePositionStart());
      }
    }
  }

  protected List<FunctionSymbol> getFittingMethods(List<FunctionSymbol> methodlist,
    ASTCallExpression expr) {
    List<FunctionSymbol> fittingMethods = new ArrayList<>();
    for (FunctionSymbol method : methodlist) {
      //for every method found check if the arguments are correct
      if (expr.getArguments().getExpressionList().size() == method.getParameterList().size()) {
        boolean success = true;
        List<SymTypeExpression> dynamicParamTypes = Lists.newArrayList();
        for(int i = 0; i<method.getParameterList().size(); i++){
          expr.getArguments().getExpression(i).accept(getTraverser());
          dynamicParamTypes.add(typeCheckResult.getCurrentResult());
        }
        SymTypeExpression ret = calculateReturnType(method, dynamicParamTypes);
        if(!ret.equals(method.getReturnType())){
          method = method.deepClone();
          method.setReturnType(ret);
        }
        for (int i = 0; i < method.getParameterList().size(); i++) {
          //test if every single argument is correct
          if (!method.getParameterList().get(i).getType()
            .deepEquals(dynamicParamTypes.get(i)) &&
            !compatible(method.getParameterList().get(i).getType(),
              dynamicParamTypes.get(i)) &&
            !method.getParameterList().get(i).getType().isTypeVariable()
          ) {
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

  protected SymTypeExpression calculateReturnType(FunctionSymbol function, List<SymTypeExpression> dynamicTypes){
    List<SymTypeExpression> typeVars = getTypeVariables(function.getReturnType());
    if(typeVars.isEmpty()){
      return function.getReturnType();
    }
    Map<String, SymTypeExpression> replacements = Maps.newHashMap();
    List<SymTypeExpression> parameterTypes = function.getParameterList()
      .stream()
      .map(VariableSymbol::getType)
      .collect(Collectors.toList());
    for(int i = 0; i<parameterTypes.size(); i++){
      List<SymTypeExpression> paramTypeVars = getTypeVariables(parameterTypes.get(i));
      List<String> returnTypeVars = typeVars.stream().map(s -> s.getTypeInfo().getName()).collect(Collectors.toList());
      for(SymTypeExpression paramTypeVar: paramTypeVars){
        if(returnTypeVars.contains(paramTypeVar.getTypeInfo().getName())){
          SymTypeExpression replacement = calculateReplacement(paramTypeVar.getTypeInfo().getName(), parameterTypes.get(i), dynamicTypes.get(i));
          SymTypeExpression otherRep = replacements.get(paramTypeVar.getTypeInfo().getName());
          if(otherRep != null && !otherRep.getTypeInfo().getName().equals(replacement.getTypeInfo().getName())){
            Log.error("Two different replacements are given for the type variable " + paramTypeVar.getTypeInfo().getName());
          }else{
            replacements.put(paramTypeVar.getTypeInfo().getName(), replacement);
          }
        }
      }
    }
    return replaceAll(function.getReturnType().deepClone(), replacements);
  }

  protected SymTypeExpression calculateReplacement(String typeName, SymTypeExpression paramType, SymTypeExpression dynamicType){
    if(paramType.getTypeInfo().getName().equals(typeName)){
      return dynamicType;
    }
    if(paramType.isGenericType() && dynamicType.isGenericType()){
      SymTypeOfGenerics param = (SymTypeOfGenerics) paramType;
      SymTypeOfGenerics dynamic = (SymTypeOfGenerics) dynamicType;
      for(int i = 0; i<param.getArgumentList().size(); i++){
        SymTypeExpression replacement = calculateReplacement(typeName, param.getArgument(i), dynamic.getArgument(i));
        if (replacement != null){
          param.setArgument(i, replacement);
        }
      }
    }else{
      Log.error("The dynamic type of a generic type must be a generic type as well");
    }
    return null;
  }

  protected SymTypeExpression replaceAll(SymTypeExpression returnType, Map<String, SymTypeExpression> replacements){
    if(returnType.isTypeVariable()){
      return replacements.get(returnType.getTypeInfo().getName());
    }else if(returnType.isGenericType()){
      SymTypeOfGenerics genReturnType = (SymTypeOfGenerics) returnType;
      List<SymTypeExpression> args = genReturnType.getArgumentList();
      List<SymTypeExpression> newArgs = Lists.newArrayList();
      for(SymTypeExpression arg : args){
        newArgs.add(replaceAll(arg, replacements));
      }
      genReturnType.setArgumentList(newArgs);
      return genReturnType;
    }else if(returnType.isArrayType()){
      SymTypeArray arrayType = (SymTypeArray) returnType;
      arrayType.setArgument(replaceAll(arrayType.getArgument(), replacements));
      return arrayType;
    }else{
      return returnType;
    }
  }

  protected List<SymTypeExpression> getTypeVariables(SymTypeExpression type){
    List<SymTypeExpression> typeVars = Lists.newArrayList();
    if(type.isTypeVariable()){
      typeVars.add(type);
    }else if(type.isArrayType()){
      typeVars.addAll(getTypeVariables(((SymTypeArray) type).getArgument()));
    }else if(type.isGenericType()){
      SymTypeOfGenerics genType = (SymTypeOfGenerics) type;
      for(SymTypeExpression arg : genType.getArgumentList()){
        typeVars.addAll(getTypeVariables(arg));
      }
    }
    return typeVars;
  }

  private boolean isNumericWithSIUnitType(SymTypeExpression type) {
    return type instanceof SymTypeOfNumericWithSIUnit;
  }

  /**
   * return the result for the "+"-operation if Strings
   *
   * @param expr
   * @param rightResult
   * @param leftResult
   */
  @Override protected Optional<SymTypeExpression> getBinaryNumericPromotionWithString(
    ASTExpression expr, SymTypeExpression rightResult, SymTypeExpression leftResult) {
    //if one part of the expression is a String then the whole expression is a String
    if (isString(leftResult) || isString(rightResult)) {
      return Optional.of(SymTypeExpressionFactory.createTypeExpression("String", MontiThingsMill.globalScope()));
    }
    //no String in the expression -> use the normal calculation for the basic arithmetic operators
    return getBinaryNumericPromotion(leftResult, rightResult);
  }
}
