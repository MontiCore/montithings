// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import montithings._ast.ASTMontiThingsNode;
import montithings._visitor.MontiThingsPrettyPrinterDelegator;
import montithings.generator.visitor.CppAssignmentPrettyPrinter;
import montithings.generator.visitor.CppCommonExpressionsPrettyPrinter;
import montithings.generator.visitor.CppExpressionPrettyPrinter;

public class CppPrettyPrinter {

  public static String print(ASTMontiThingsNode node) {
    return getPrinter().prettyprint(node);
  }

  public static String print(ASTExpression node) {
    return getPrinter().prettyprint(node);
  }

  public static MontiThingsPrettyPrinterDelegator getPrinter() {
    MontiThingsPrettyPrinterDelegator printer = new MontiThingsPrettyPrinterDelegator();
    printer.setExpressionsBasisVisitor(new CppExpressionPrettyPrinter(printer.getPrinter()));
    printer.setCommonExpressionsVisitor(new CppCommonExpressionsPrettyPrinter(printer.getPrinter()));
    printer.setAssignmentExpressionsVisitor(new CppAssignmentPrettyPrinter(printer.getPrinter()));
    return printer;
  }
}
