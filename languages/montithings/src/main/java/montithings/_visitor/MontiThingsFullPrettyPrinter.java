// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._visitor.ArcBasisPrettyPrinter;
import behavior._visitor.BehaviorPrettyPrinter;
import clockcontrol._visitor.ClockControlPrettyPrinter;
import comfortablearc._visitor.ComfortableArcPrettyPrinter;
import conditionbasis._visitor.ConditionBasisPrettyPrinter;
import conditioncatch._visitor.ConditionCatchPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.ocl.optionaloperators.prettyprint.OptionalOperatorsPrettyPrinter;
import de.monticore.ocl.setexpressions.prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals.prettyprint.SIUnitLiteralsPrettyPrinter;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import genericarc._visitor.GenericArcPrettyPrinter;
import montiarc._ast.ASTMontiArcNode;
import montiarc._visitor.MontiArcPrettyPrinter;
import montithings.MontiThingsMill;
import montithings._ast.ASTMontiThingsNode;
import portextensions._visitor.PortExtensionsPrettyPrinter;
import prepostcondition._visitor.PrePostConditionPrettyPrinter;
import setdefinitions._visitor.SetDefinitionsPrettyPrinter;

public class MontiThingsFullPrettyPrinter {
  protected IndentPrinter printer;

  protected MontiThingsTraverser traverser;

  public MontiThingsFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public MontiThingsFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    traverser = MontiThingsMill.traverser();

    MontiArcPrettyPrinter maPP = new MontiArcPrettyPrinter(printer);
    ClockControlPrettyPrinter ccPP = new ClockControlPrettyPrinter(printer);
    PortExtensionsPrettyPrinter pePP = new PortExtensionsPrettyPrinter(printer);
    ConditionBasisPrettyPrinter cbPP = new ConditionBasisPrettyPrinter(printer);
    PrePostConditionPrettyPrinter ppcPP = new PrePostConditionPrettyPrinter(printer);
    ConditionCatchPrettyPrinter concPP = new ConditionCatchPrettyPrinter(printer);
    SetDefinitionsPrettyPrinter sdPP = new SetDefinitionsPrettyPrinter(printer);
    SetExpressionsPrettyPrinter sePP = new SetExpressionsPrettyPrinter(printer);
    OCLExpressionsPrettyPrinter oclePP = new OCLExpressionsPrettyPrinter(printer);
    OptionalOperatorsPrettyPrinter ooPP = new OptionalOperatorsPrettyPrinter(printer);
    BehaviorPrettyPrinter bPP = new BehaviorPrettyPrinter(printer);

    traverser.setMontiArcHandler(maPP);
    traverser.setClockControlHandler(ccPP);
    traverser.setPortExtensionsHandler(pePP);
    traverser.setConditionBasisHandler(cbPP);
    traverser.setPrePostConditionHandler(ppcPP);
    traverser.setConditionCatchHandler(concPP);
    traverser.setSetDefinitionsHandler(sdPP);
    traverser.setSetExpressionsHandler(sePP);
    traverser.setOCLExpressionsHandler(oclePP);
    traverser.setOptionalOperatorsHandler(ooPP);
    traverser.setBehaviorHandler(bPP);

    MCCommonLiteralsPrettyPrinter mccommonliteralspp = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.setMCCommonLiteralsHandler(mccommonliteralspp);
    traverser.add4MCCommonLiterals(mccommonliteralspp);
    CommonExpressionsPrettyPrinter commonExpressionsPrettyPrinter = new CommonExpressionsPrettyPrinter(printer);
    traverser.setCommonExpressionsHandler(commonExpressionsPrettyPrinter);
    traverser.add4CommonExpressions(commonExpressionsPrettyPrinter);
    AssignmentExpressionsPrettyPrinter assignmentExpressionsPrettyPrinter = new AssignmentExpressionsPrettyPrinter(printer);
    traverser.setAssignmentExpressionsHandler(assignmentExpressionsPrettyPrinter);
    traverser.add4AssignmentExpressions(assignmentExpressionsPrettyPrinter);
    MCSimpleGenericTypesPrettyPrinter mcSimpleGenericTypesPrettyPrinter = new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.setMCSimpleGenericTypesHandler(mcSimpleGenericTypesPrettyPrinter);
    traverser.add4MCSimpleGenericTypes(mcSimpleGenericTypesPrettyPrinter);
    MCCommonStatementsPrettyPrinter mcCommonStatementsPrettyPrinter = new MCCommonStatementsPrettyPrinter(printer);
    traverser.setMCCommonStatementsHandler(mcCommonStatementsPrettyPrinter);
    traverser.add4MCCommonStatements(mcCommonStatementsPrettyPrinter);
    MCVarDeclarationStatementsPrettyPrinter mcVarDeclarationStatementsPrettyPrinter = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.setMCVarDeclarationStatementsHandler(mcVarDeclarationStatementsPrettyPrinter);
    traverser.add4MCVarDeclarationStatements(mcVarDeclarationStatementsPrettyPrinter);

    ExpressionsBasisPrettyPrinter expressionsBasisPrettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    traverser.setExpressionsBasisHandler(expressionsBasisPrettyPrinter);
    traverser.add4ExpressionsBasis(expressionsBasisPrettyPrinter);
    MCCollectionTypesPrettyPrinter mcCollectionTypesPrettyPrinter = new MCCollectionTypesPrettyPrinter(printer);
    traverser.setMCCollectionTypesHandler(mcCollectionTypesPrettyPrinter);
    traverser.add4MCCollectionTypes(mcCollectionTypesPrettyPrinter);
    MCBasicTypesPrettyPrinter mcBasicTypesPrettyPrinter = new MCBasicTypesPrettyPrinter(printer);
    traverser.setMCBasicTypesHandler(mcBasicTypesPrettyPrinter);
    traverser.add4MCBasicTypes(mcBasicTypesPrettyPrinter);
    ArcBasisPrettyPrinter arcBasisPrettyPrinter = new ArcBasisPrettyPrinter(printer);
    traverser.setArcBasisHandler(arcBasisPrettyPrinter);
    ComfortableArcPrettyPrinter comfortableArcPrettyPrinter = new ComfortableArcPrettyPrinter(printer);
    traverser.setComfortableArcHandler(comfortableArcPrettyPrinter);
    GenericArcPrettyPrinter genericArcPrettyPrinter = new GenericArcPrettyPrinter(printer);
    traverser.setGenericArcHandler(genericArcPrettyPrinter);

    SIUnitTypes4ComputingPrettyPrinter siunittypes4computingpp = new SIUnitTypes4ComputingPrettyPrinter(printer);
    traverser.setSIUnitTypes4ComputingHandler(siunittypes4computingpp);
    SIUnitsPrettyPrinter siunitspp = new SIUnitsPrettyPrinter(printer);
    traverser.setSIUnitsHandler(siunitspp);
    traverser.add4SIUnits(siunitspp);
    SIUnitLiteralsPrettyPrinter siunitliteralspp = new SIUnitLiteralsPrettyPrinter(printer);
    traverser.setSIUnitLiteralsHandler(siunitliteralspp);

    MontiThingsPrettyPrinter montithingspp = new MontiThingsPrettyPrinter(printer);
    traverser.setMontiThingsHandler(montithingspp);
  }

  public String prettyprint(ASTMontiThingsNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMontiArcNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpression a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCJavaBlock a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCType a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public MontiThingsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MontiThingsTraverser traverser) {
    this.traverser = traverser;
  }
}
