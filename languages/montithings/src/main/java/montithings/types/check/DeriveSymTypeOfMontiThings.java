package montithings.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.*;
import montithings._visitor.MontiThingsDelegatorVisitor;

import java.util.Optional;

public class DeriveSymTypeOfMontiThings extends MontiThingsDelegatorVisitor implements ITypesCalculator {

  private MontiThingsDelegatorVisitor realThis;

  private DeriveSymTypeOfAssignmentExpressionsWithSIUnitTypes deriveSymTypeOfAssignmentExpressions;

  private DeriveSymTypeOfCommonExpressionsWithSIUnitTypes deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfExpression deriveSymTypeOfExpression;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfSIUnitLiterals deriveSymTypeOfSIUnitLiterals;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private TypeCheckResult typeCheckResult = new TypeCheckResult();


  public DeriveSymTypeOfMontiThings() {
    this.realThis = this;
    init();
  }

  /**
   * main method to calculate the type of an expression
   */
  public Optional<SymTypeExpression> calculateType(ASTExpression e) {
    e.accept(realThis);
    Optional<SymTypeExpression> result = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      result = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.setCurrentResultAbsent();
    return result;
  }

  @Override
  public MontiThingsDelegatorVisitor getRealThis() {
    return realThis;
  }

  /**
   * set the last result of all calculators to the same object
   */
  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    deriveSymTypeOfAssignmentExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfSIUnitLiterals.setTypeCheckResult(typeCheckResult);
  }

  /**
   * initialize the typescalculator
   */
  @Override
  public void init() {
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressionsWithSIUnitTypes();
    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressionsWithSIUnitTypes();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfSIUnitLiterals = new DeriveSymTypeOfSIUnitLiterals();

    setCommonExpressionsVisitor(deriveSymTypeOfCommonExpressions);
    setAssignmentExpressionsVisitor(deriveSymTypeOfAssignmentExpressions);
    setExpressionsBasisVisitor(deriveSymTypeOfExpression);
    setMCLiteralsBasisVisitor(deriveSymTypeOfLiterals);
    setMCCommonLiteralsVisitor(deriveSymTypeOfMCCommonLiterals);
    setSIUnitLiteralsVisitor(deriveSymTypeOfSIUnitLiterals);
    setMontiThingsVisitor(new MontiThingsDelegatorVisitor());

    setTypeCheckResult(typeCheckResult);
  }

  /**
   * main method to calculate the type of a literal
   */
  @Override
  public Optional<SymTypeExpression> calculateType(ASTLiteral lit) {
    lit.accept(realThis);
    Optional<SymTypeExpression> result = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      result = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.setCurrentResultAbsent();
    return result;
  }

  /**
   * main method to calculate the type of a signed literal
   */
  @Override
  public Optional<SymTypeExpression> calculateType(ASTSignedLiteral lit) {
    lit.accept(realThis);
    Optional<SymTypeExpression> result = Optional.empty();
    if (typeCheckResult.isPresentCurrentResult()) {
      result = Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    typeCheckResult.setCurrentResultAbsent();
    return result;
  }

}
