// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import de.monticore.ocl.oclexpressions._ast.ASTEquivalentExpression;
import de.monticore.ocl.oclexpressions._ast.ASTIfThenElseExpression;
import de.monticore.ocl.oclexpressions._ast.ASTImpliesExpression;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class CppOCLExpressionsPrettyPrinter extends OCLExpressionsPrettyPrinter {

  public CppOCLExpressionsPrettyPrinter(IndentPrinter printer) {
    super(printer);
    this.realThis = this;
  }

  @Override
  public void handle(ASTIfThenElseExpression node) {
    getPrinter().print("if (");
    node.getCondition().accept(getRealThis());
    getPrinter().println(") {");
    node.getThenExpression().accept(getRealThis());
    getPrinter().println();
    getPrinter().println(" }");
    getPrinter().println("else {");
    node.getElseExpression().accept(getRealThis());
    getPrinter().println();
    getPrinter().println("}");
  }

  @Override
  public void handle(ASTImpliesExpression node) {
    getPrinter().print("(((");
    node.getLeft().accept(getRealThis());
    getPrinter().print(") && (");
    node.getRight().accept(getRealThis());
    getPrinter().print(")) || ( !(");
    node.getLeft().accept(getRealThis());
    getPrinter().print(")))");
  }

  @Override
  public void handle(ASTEquivalentExpression node) {
    getPrinter().print("(((");
    node.getLeft().accept(getRealThis());
    getPrinter().print(") && (");
    node.getRight().accept(getRealThis());
    getPrinter().print(")) || (( !(");
    node.getLeft().accept(getRealThis());
    getPrinter().print(")) && ( !(");
    node.getRight().accept(getRealThis());
    getPrinter().print("))))");
  }
}
