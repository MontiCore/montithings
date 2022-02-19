// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montithings._ast.ASTMontiThingsNode;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import montithings.generator.visitor.MontiThingsSIUnitLiteralsPrettyPrinter;

import java.util.Stack;

public class CppPrettyPrinter {

  public static String print(ASTMontiThingsNode node) {
    return getPrinter().prettyprint(node);
  }

  public static String print(ASTExpression node) {
    return getPrinter().prettyprint(node);
  }

  public static MontiThingsFullPrettyPrinter getPrinter() {
    return getPrinter(false, false);
  }

  public static MontiThingsFullPrettyPrinter getPrinter(boolean isLogTracingEnabled, boolean suppressPostConditionCheck) {
    MontiThingsFullPrettyPrinter printer = new MontiThingsFullPrettyPrinter();
    printer.getTraverser().setExpressionsBasisHandler(new CppExpressionPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setCommonExpressionsHandler(new CppCommonExpressionsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setAssignmentExpressionsHandler(new CppAssignmentPrettyPrinter(printer.getPrinter(), isLogTracingEnabled, suppressPostConditionCheck));
    printer.getTraverser().setOCLExpressionsHandler(new CppOCLExpressionsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setOptionalOperatorsHandler(new CppOptionalOperatorsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setSIUnitLiteralsHandler(new MontiThingsSIUnitLiteralsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setMCVarDeclarationStatementsHandler(new CppVarDeclarationStatementsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setBehaviorHandler(new CppBehaviorPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setMCCommonStatementsHandler(new CppMCCommonStatementsPrettyPrinter(printer.getPrinter()));


    CppMontiThingsPrettyPrinter setPrinter = new CppMontiThingsPrettyPrinter(printer.getPrinter());
    Stack<ASTExpression> expressions = new Stack<>();
    CppSetDefinitionsPrettyPrinter setDefinitionsPrettyPrinter = new CppSetDefinitionsPrettyPrinter(printer.getPrinter());
    CppSetExpressionsPrettyPrinter setExpressionsPrettyPrinter = new CppSetExpressionsPrettyPrinter(printer.getPrinter());
    setDefinitionsPrettyPrinter.setExpressions(expressions);
    setExpressionsPrettyPrinter.setExpressions(expressions);
    printer.getTraverser().setSetDefinitionsHandler(setDefinitionsPrettyPrinter);
    printer.getTraverser().setSetExpressionsHandler(setExpressionsPrettyPrinter);
    printer.getTraverser().setMontiThingsHandler(setPrinter);
    printer.getTraverser().setSIUnitsHandler(new CppSIUnitsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setSIUnitTypes4ComputingHandler(new CppSIUnitTypes4ComputingPrettyPrinter(printer.getPrinter()));
    return printer;
  }
}
