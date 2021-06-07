// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import behavior.types.check.DeriveSymTypeOfBehavior;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.ocl.types.check.DeriveSymTypeOfOCLExpressions;
import de.monticore.ocl.types.check.DeriveSymTypeOfSetExpressions;
import de.monticore.types.check.*;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;
import types.check.DeriveSymTypeOfSetDefinitions;

import java.util.Optional;

public class DeriveSymTypeOfMontiThingsCombine
  implements IDerive {

  private MontiThingsTraverser traverser;

  private DeriveSymTypeOfAssignmentExpressionsForMT deriveSymTypeOfAssignmentExpressions;

  private DeriveSymTypeOfCommonExpressionsForMT deriveSymTypeOfCommonExpressions;

  private DeriveSymTypeOfOCLExpressions deriveSymTypeOfOCLExpressions;

  private DeriveSymTypeOfExpressionForMT deriveSymTypeOfExpression;

  private DeriveSymTypeOfLiterals deriveSymTypeOfLiterals;

  private DeriveSymTypeOfSIUnitLiterals deriveSymTypeOfSIUnitLiterals;

  private DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals;

  private DeriveSymTypeOfMontiThings deriveSymTypeOfMontiThings;

  private DeriveSymTypeOfSetDefinitions deriveSymTypeOfSetDefinitions;

  private DeriveSymTypeOfSetExpressions deriveSymTypeOfSetExpressions;

  private DeriveSymTypeOfBehavior deriveSymTypeOfBehavior;

  private SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes;

  private SynthesizeSymTypeFromMCSimpleGenericTypes synthesizeSymTypeFromMCSimpleGenericTypes;

  private SynthesizeSymTypeFromSIUnitTypes4Computing synthesizeSymTypeFromSIUnitTypes4Computing;

  private SynthesizeSymTypeFromMCCollectionTypes synthesizeSymTypeFromMCCollectionTypes;


  private TypeCheckResult typeCheckResult = new TypeCheckResult();

  public DeriveSymTypeOfMontiThingsCombine() {
    init();
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
    deriveSymTypeOfMontiThings.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfOCLExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfSetDefinitions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfSetExpressions.setTypeCheckResult(typeCheckResult);
    deriveSymTypeOfBehavior.setTypeCheckResult(typeCheckResult);
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(typeCheckResult);
    synthesizeSymTypeFromMCCollectionTypes.setTypeCheckResult(typeCheckResult);
    synthesizeSymTypeFromMCSimpleGenericTypes.setTypeCheckResult(typeCheckResult);
    synthesizeSymTypeFromSIUnitTypes4Computing.setTypeCheckResult(typeCheckResult);
  }

  @Override public Optional<SymTypeExpression> getResult() {
    if(typeCheckResult.isPresentCurrentResult()){
      return Optional.ofNullable(typeCheckResult.getCurrentResult());
    }
    return Optional.empty();
  }

  /**
   * initialize the typescalculator
   */
  @Override
  public void init() {
    traverser = MontiThingsMill.traverser();
    deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressionsForMT();
    deriveSymTypeOfAssignmentExpressions = new DeriveSymTypeOfAssignmentExpressionsForMT();
    deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfExpression = new DeriveSymTypeOfExpressionForMT();
    deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfSIUnitLiterals = new DeriveSymTypeOfSIUnitLiterals();
    deriveSymTypeOfMontiThings = new DeriveSymTypeOfMontiThings();
    deriveSymTypeOfOCLExpressions = new DeriveSymTypeOfOCLExpressionsForMT();
    deriveSymTypeOfSetDefinitions = new DeriveSymTypeOfSetDefinitions();
    deriveSymTypeOfSetExpressions = new DeriveSymTypeOfSetExpressions();
    deriveSymTypeOfBehavior = new DeriveSymTypeOfBehavior();
    synthesizeSymTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromSIUnitTypes4Computing = new SynthesizeSymTypeFromSIUnitTypes4Computing();
    synthesizeSymTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synthesizeSymTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();

    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);
    traverser.setAssignmentExpressionsHandler(deriveSymTypeOfAssignmentExpressions);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);
    traverser.setSIUnitLiteralsHandler(deriveSymTypeOfSIUnitLiterals);
    traverser.setMontiThingsHandler(deriveSymTypeOfMontiThings);
    traverser.setOCLExpressionsHandler(deriveSymTypeOfOCLExpressions);
    traverser.setSetDefinitionsHandler(deriveSymTypeOfSetDefinitions);
    traverser.setSetExpressionsHandler(deriveSymTypeOfSetExpressions);
    traverser.setBehaviorHandler(deriveSymTypeOfBehavior);
    traverser.setMCBasicTypesHandler(synthesizeSymTypeFromMCBasicTypes);
    traverser.setMCCollectionTypesHandler(synthesizeSymTypeFromMCCollectionTypes);
    traverser.setSIUnitTypes4ComputingHandler(synthesizeSymTypeFromSIUnitTypes4Computing);
    traverser.setMCSimpleGenericTypesHandler(synthesizeSymTypeFromMCSimpleGenericTypes);

    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.add4AssignmentExpressions(deriveSymTypeOfAssignmentExpressions);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.add4SetExpressions(deriveSymTypeOfSetExpressions);
    traverser.add4MCBasicTypes(synthesizeSymTypeFromMCBasicTypes);
    traverser.add4MCCollectionTypes(synthesizeSymTypeFromMCCollectionTypes);
    traverser.add4MCSimpleGenericTypes(synthesizeSymTypeFromMCSimpleGenericTypes);

    setTypeCheckResult(typeCheckResult);
  }

  public TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  public void setTraverser(MontiThingsTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MontiThingsTraverser getTraverser() {
    return traverser;
  }
}
