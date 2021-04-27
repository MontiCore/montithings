// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._visitor.ArcBasisPrettyPrinter;
import arccore._visitor.ArcCorePrettyPrinter;
import behavior._visitor.BehaviorPrettyPrinter;
import clockcontrol._visitor.ClockControlPrettyPrinter;
import comfortablearc._visitor.ComfortableArcPrettyPrinter;
import conditionbasis._visitor.ConditionBasisPrettyPrinter;
import conditioncatch._visitor.ConditionCatchPrettyPrinter;
import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.ocl.optionaloperators.prettyprint.OptionalOperatorsPrettyPrinter;
import de.monticore.ocl.setexpressions.prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.siunitliterals.prettyprint.SIUnitLiteralsPrettyPrinter;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;
import de.monticore.siunittypes4math.prettyprint.SIUnitTypes4MathPrettyPrinter;
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
import montithings._ast.ASTMontiThingsNode;
import portextensions._visitor.PortExtensionsPrettyPrinter;
import prepostcondition._visitor.PrePostConditionPrettyPrinter;
import setdefinitions._visitor.SetDefinitionsPrettyPrinter;

public class MontiThingsPrettyPrinterDelegator extends MontiThingsDelegatorVisitor {

  protected MontiThingsPrettyPrinterDelegator realThis = this;

  protected IndentPrinter printer = null;

  public MontiThingsPrettyPrinterDelegator() {
    this(new IndentPrinter());
  }

  public MontiThingsPrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
    setMontiArcVisitor(new MontiArcPrettyPrinter(printer));
    setClockControlVisitor(new ClockControlPrettyPrinter(printer));
    setPortExtensionsVisitor(new PortExtensionsPrettyPrinter(printer));
    setConditionBasisVisitor(new ConditionBasisPrettyPrinter(printer));
    setPrePostConditionVisitor(new PrePostConditionPrettyPrinter(printer));
    setConditionCatchVisitor(new ConditionCatchPrettyPrinter(printer));
    setSetDefinitionsVisitor(new SetDefinitionsPrettyPrinter(printer));
    setSetExpressionsVisitor(new SetExpressionsPrettyPrinter(printer));
    setOCLExpressionsVisitor(new OCLExpressionsPrettyPrinter(printer));
    setOptionalOperatorsVisitor(new OptionalOperatorsPrettyPrinter(printer));
    setBehaviorVisitor(new BehaviorPrettyPrinter(printer));

    setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(printer));
    setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(printer));
    setAssignmentExpressionsVisitor(new AssignmentExpressionsPrettyPrinter(printer));
    setMCSimpleGenericTypesVisitor(new MCSimpleGenericTypesPrettyPrinter(printer));
    setMCCommonStatementsVisitor(new MCCommonStatementsPrettyPrinter(printer));
    setMCVarDeclarationStatementsVisitor(new MCVarDeclarationStatementsPrettyPrinter(printer));
    setArcCoreVisitor(new ArcCorePrettyPrinter(printer));

    setMCBasicsVisitor(new MCBasicsPrettyPrinter(printer));
    setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(printer));
    setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(printer));
    setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(printer));
    setArcBasisVisitor(new ArcBasisPrettyPrinter(printer));
    setComfortableArcVisitor(new ComfortableArcPrettyPrinter(printer));
    setGenericArcVisitor(new GenericArcPrettyPrinter(printer));

    setSIUnitTypes4ComputingVisitor(new SIUnitTypes4ComputingPrettyPrinter(printer));
    setSIUnitTypes4MathVisitor(new SIUnitTypes4MathPrettyPrinter(printer));
    setSIUnitsVisitor(new SIUnitsPrettyPrinter(printer));
    setSIUnitLiteralsVisitor(new SIUnitLiteralsPrettyPrinter(printer));

    setMontiThingsVisitor(new MontiThingsPrettyPrinter(printer));
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public String prettyprint(ASTMontiThingsNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMontiArcNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpression a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCJavaBlock a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMCType a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  @Override
  public MontiThingsPrettyPrinterDelegator getRealThis() {
    return realThis;
  }
}
