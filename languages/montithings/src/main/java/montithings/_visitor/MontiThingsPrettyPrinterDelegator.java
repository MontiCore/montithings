package montithings._visitor;

import arcbasis._visitor.ArcBasisPrettyPrinter;
import arccore._visitor.ArcCorePrettyPrinter;
import clockcontrol._visitor.ClockControlPrettyPrinter;
import comfortablearc._visitor.ComfortableArcPrettyPrinter;
import conditionbasis._visitor.ConditionBasisPrettyPrinter;
import conditioncatch._visitor.ConditionCatchPrettyPrinter;
import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
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
    this.printer = new IndentPrinter();
    realThis = this;
    setMontiArcVisitor(new MontiArcPrettyPrinter(printer));
    setClockControlVisitor(new ClockControlPrettyPrinter(printer));
    setPortExtensionsVisitor(new PortExtensionsPrettyPrinter(printer));
    setConditionBasisVisitor(new ConditionBasisPrettyPrinter(printer));
    setPrePostConditionVisitor(new PrePostConditionPrettyPrinter(printer));
    setConditionCatchVisitor(new ConditionCatchPrettyPrinter(printer));
    setSetDefinitionsVisitor(new SetDefinitionsPrettyPrinter(printer));
    setSetExpressionsVisitor(new SetExpressionsPrettyPrinter(printer));

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

    setMontiThingsVisitor(new MontiThingsPrettyPrinter(printer));
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

    setMontiThingsVisitor(new MontiThingsPrettyPrinter(printer));
  }

  protected IndentPrinter getPrinter() {
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

  @Override
  public MontiThingsPrettyPrinterDelegator getRealThis() {
    return realThis;
  }
}
