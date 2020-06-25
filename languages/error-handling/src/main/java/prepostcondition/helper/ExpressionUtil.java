/* (c) https://github.com/MontiCore/monticore */
package prepostcondition.helper;

import prepostcondition.visitor.GuardExpressionVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;

public class ExpressionUtil {

  public static String printExpression(ASTExpression node) {
    IndentPrinter printer = new IndentPrinter();
    ExpressionsBasisPrettyPrinter prettyPrinter = new ExpressionsBasisPrettyPrinter(printer);
    node.accept(prettyPrinter);
    return printer.getContent();
  }

  /**
   * Returns all NameExpressions that appear in the guard of the execution statement
   *
   * @param node
   * @return
   */
  private static List<ASTNameExpression> getNameExpressionElements(ASTExpression node) {
    GuardExpressionVisitor visitor = new GuardExpressionVisitor();
    node.accept(visitor);
    return visitor.getExpressions();
  }
}
