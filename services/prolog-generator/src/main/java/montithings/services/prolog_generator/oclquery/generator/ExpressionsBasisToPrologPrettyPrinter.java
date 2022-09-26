package montithings.services.prolog_generator.oclquery.generator;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montithings.services.prolog_generator.Utils;

public class ExpressionsBasisToPrologPrettyPrinter extends ExpressionsBasisPrettyPrinter {
  public ExpressionsBasisToPrologPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTNameExpression node) {
    getPrinter().print(Utils.capitalize(node.getName()));
  }
}
