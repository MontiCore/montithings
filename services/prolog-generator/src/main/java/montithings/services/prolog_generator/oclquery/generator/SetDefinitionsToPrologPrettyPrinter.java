package montithings.services.prolog_generator.oclquery.generator;

import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import setdefinitions._ast.ASTListExpression;
import setdefinitions._ast.ASTMapExpression;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._visitor.SetDefinitionsPrettyPrinter;

public class SetDefinitionsToPrologPrettyPrinter extends SetDefinitionsPrettyPrinter {

  public SetDefinitionsToPrologPrettyPrinter(IndentPrinter indentPrinter) {
    super(indentPrinter);
  }

  @Override
  public void handle(ASTSetValueRange node) {
    getPrinter().print("betweeen(");
    node.getLowerBound().accept(getTraverser());
    getPrinter().print(",");
    node.getUpperBound().accept(getTraverser());
    getPrinter().print(")");
    if (node.isPresentStepsize()) {
      Log.error("Step size in SetValueRanges is not currently supported by the prolog generator");
    }
  }

  @Override
  public void handle(ASTListExpression node) {
    for (int i = 0; i < node.sizeExpressions(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      node.getExpression(i).accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTMapExpression node) {
    Log.error("MapExpressions are not currently supported by the prolog generator");
  }
}
