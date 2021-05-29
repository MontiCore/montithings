// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._visitor.ArcBasisPrettyPrinter;
import arccore._visitor.ArcCorePrettyPrinter;
import comfortablearc._visitor.ComfortableArcPrettyPrinter;
import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
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

public class MontiThingsToMontiArcPrettyPrinterDelegator extends MontiThingsDelegatorVisitor {

  protected MontiThingsToMontiArcPrettyPrinterDelegator realThis = this;

  protected IndentPrinter printer = null;

  public MontiThingsToMontiArcPrettyPrinterDelegator() {
    this(new IndentPrinter());
  }

  public MontiThingsToMontiArcPrettyPrinterDelegator(IndentPrinter printer) {
    this.printer = printer;
    realThis = this;
    setMontiArcVisitor(new MontiArcPrettyPrinter(printer));

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
    setSIUnitTypes4MathVisitor(new SIUnitTypePrimitivePrettyPrinter(printer));
    setSIUnitsVisitor(new SIUnitPrimitivePrettyPrinter(printer));
    setSIUnitLiteralsVisitor(new SIUnitLiteralsPrettyPrinter(printer));
    
    setMontiThingsVisitor(new MontiThingsToMontiArcPrettyPrinter(printer));
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
  public MontiThingsToMontiArcPrettyPrinterDelegator getRealThis() {
    return realThis;
  }
}
