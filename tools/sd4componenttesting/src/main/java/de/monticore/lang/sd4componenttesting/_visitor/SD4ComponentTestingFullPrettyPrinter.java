// (c) https://github.com/MontiCore/monticore

package de.monticore.lang.sd4componenttesting._visitor;

import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortAccess;
import arcbasis._visitor.ArcBasisPrettyPrinter;
import comfortablearc._visitor.ComfortableArcPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.lang.sd4componenttesting.SD4ComponentTestingMill;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CExpression;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4ComponentTestingNode;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.ocl.optionaloperators.prettyprint.OptionalOperatorsPrettyPrinter;
import de.monticore.ocl.setexpressions.prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.SCBasisPrettyPrinter;
import de.monticore.prettyprint.SCTransitions4CodePrettyPrinter;
import de.monticore.statements.prettyprint.MCCommonStatementsPrettyPrinter;
import de.monticore.statements.prettyprint.MCVarDeclarationStatementsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import genericarc._visitor.GenericArcPrettyPrinter;
import montiarc._visitor.MontiArcPrettyPrinter;


public class SD4ComponentTestingFullPrettyPrinter {
  protected IndentPrinter printer;

  protected SD4ComponentTestingTraverser traverser;

  public SD4ComponentTestingFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public SD4ComponentTestingFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    traverser = SD4ComponentTestingMill.traverser();

    MontiArcPrettyPrinter maPP = new MontiArcPrettyPrinter(printer);
    SetExpressionsPrettyPrinter sePP = new SetExpressionsPrettyPrinter(printer);
    OCLExpressionsPrettyPrinter oclePP = new OCLExpressionsPrettyPrinter(printer);
    OptionalOperatorsPrettyPrinter ooPP = new OptionalOperatorsPrettyPrinter(printer);

    traverser.setMontiArcHandler(maPP);
    traverser.setOCLExpressionsHandler(oclePP);

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

    SCBasisPrettyPrinter scBasisPrettyPrinter = new SCBasisPrettyPrinter(printer);
    traverser.setSCBasisHandler(scBasisPrettyPrinter);
    SCTransitions4CodePrettyPrinter scTransitions4CodePrettyPrinter = new SCTransitions4CodePrettyPrinter(printer);
    traverser.setSCTransitions4CodeHandler(scTransitions4CodePrettyPrinter);

    SD4ComponentTestingPrettyPrinter sd4ComponentTestingPrettyPrinter = new SD4ComponentTestingPrettyPrinter(printer);
    traverser.setSD4ComponentTestingHandler(sd4ComponentTestingPrettyPrinter);

  }

  public String prettyprint(ASTSD4ComponentTestingNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTPortAccess a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTExpression a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTSD4CExpression a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTComponentType a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }


  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public SD4ComponentTestingTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(SD4ComponentTestingTraverser traverser) {
    this.traverser = traverser;
  }
}
