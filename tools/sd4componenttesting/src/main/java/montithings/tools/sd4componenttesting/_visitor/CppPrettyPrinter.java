// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montithings.tools.sd4componenttesting._ast.ASTSD4CExpression;
import montithings.tools.sd4componenttesting._ast.ASTSD4ComponentTestingNode;
import montithings.generator.prettyprinter.*;

import java.util.Stack;

public class CppPrettyPrinter {

  public static String print(ASTSD4ComponentTestingNode node) {
    return getPrinter().prettyprint(node);
  }

  public static String print(ASTSD4CExpression node) {
    return getPrinter().prettyprint(node);
  }

  public static SD4ComponentTestingFullPrettyPrinter getPrinter() {
    return getPrinter(false, false);
  }

  public static SD4ComponentTestingFullPrettyPrinter getPrinter(boolean isLogTracingEnabled, boolean suppressPostConditionCheck) {
    SD4ComponentTestingFullPrettyPrinter printer = new SD4ComponentTestingFullPrettyPrinter();
    printer.getTraverser().setExpressionsBasisHandler(new CppExpressionPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setCommonExpressionsHandler(new CppCommonExpressionsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setAssignmentExpressionsHandler(new CppAssignmentPrettyPrinter(printer.getPrinter(), isLogTracingEnabled, suppressPostConditionCheck));
    printer.getTraverser().setOCLExpressionsHandler(new CppOCLExpressionsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setMCVarDeclarationStatementsHandler(new CppVarDeclarationStatementsPrettyPrinter(printer.getPrinter()));
    printer.getTraverser().setMCCommonStatementsHandler(new CppMCCommonStatementsPrettyPrinter(printer.getPrinter()));

    Stack<ASTExpression> expressions = new Stack<>();
    CppSetDefinitionsPrettyPrinter setDefinitionsPrettyPrinter = new CppSetDefinitionsPrettyPrinter(printer.getPrinter());
    CppSetExpressionsPrettyPrinter setExpressionsPrettyPrinter = new CppSetExpressionsPrettyPrinter(printer.getPrinter());
    setExpressionsPrettyPrinter.setExpressions(expressions);
    return printer;
  }
}
