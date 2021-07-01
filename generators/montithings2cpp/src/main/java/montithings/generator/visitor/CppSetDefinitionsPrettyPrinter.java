// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.prettyprint.IndentPrinter;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._ast.ASTSetValueRegEx;
import setdefinitions._visitor.SetDefinitionsPrettyPrinter;

import java.util.Stack;


public class CppSetDefinitionsPrettyPrinter extends SetDefinitionsPrettyPrinter {

  Stack<ASTExpression> expressions;

  public CppSetDefinitionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void handle(ASTSetValueRegEx node) {
    getPrinter().print("(");
    getPrinter().print("std::regex_match(");
    getPrinter().print("((std::ostringstream&)(std::ostringstream(\"\") << ");
    expressions.peek().accept(getTraverser());
    getPrinter().print(")).str(), ");

    getPrinter().print("std::regex(");
    node.getFormat().accept(getTraverser());
    getPrinter().print(")))");
  }

  @Override
  public void handle(ASTSetValueRange node) {
    getPrinter().print("(");
    expressions.peek().accept(getTraverser());
    getPrinter().print(" >= ");
    node.getLowerBound().accept(getTraverser());
    getPrinter().print(" && ");

    if (node.isPresentStepsize()) {
      getPrinter().print("((");
      expressions.peek().accept(getTraverser());
      getPrinter().print(" - ");
      node.getLowerBound().accept(getTraverser());
      getPrinter().print(")");
      getPrinter().print(" % ");
      node.getStepsize().accept(getTraverser());
      getPrinter().print(" == 0 ");
      getPrinter().print(") && ");
    }
    expressions.peek().accept(getTraverser());
    getPrinter().print(" <= ");
    node.getUpperBound().accept(getTraverser());
    getPrinter().print(")");
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Stack<ASTExpression> getExpressions() {
    return expressions;
  }

  public void setExpressions(
    Stack<ASTExpression> expressions) {
    this.expressions = expressions;
  }
}
