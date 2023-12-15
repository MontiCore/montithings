// (c) https://github.com/MontiCore/monticore
package montithings.services.prolog_generator.oclquery.generator;

import de.monticore.ocl.setexpressions._ast.*;
import de.monticore.ocl.setexpressions.prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

public class SetExpressionsToPrologPrettyPrinter extends SetExpressionsPrettyPrinter {

  private int index;

  public String getOperations() {
    return operations;
  }

  private String operations;

  public SetExpressionsToPrologPrettyPrinter(IndentPrinter printer) {
    super(printer);
    index = 0;
    operations = "";
  }

  @Override
  public void handle(ASTSetInExpression node) {
    if (node.getSet() instanceof ASTSetEnumeration) {
      getPrinter().print("between(");
      node.getSet().accept(getTraverser());
      getPrinter().print(", ");
      node.getElem().accept(getTraverser());
      getPrinter().print(")");
    }
    else {
      Log.error("SetExpressions other than SetEnumeration are currently not supported" +
              "in SetInExpressions by the prolog generator");
    }
  }

  @Override
  public void handle(ASTUnionExpression node) {
    getPrinter().print("_Union_" + index);
    String temp = getPrinter().getContent();
    getPrinter().clearBuffer();
    getPrinter().print("union(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(", ");
    node.getRight().accept(getTraverser());
    getPrinter().print(", _Union_" + index + ")");
    operations += ", " + getPrinter().getContent();
    getPrinter().clearBuffer();
    getPrinter().print(temp);
    index++;
  }

  @Override
  public void handle(ASTIntersectionExpression node) {
    getPrinter().print("_Intersection_" + index);
    String temp = getPrinter().getContent();
    getPrinter().clearBuffer();
    getPrinter().print("intersection(");
    node.getLeft().accept(getTraverser());
    getPrinter().print(", ");
    node.getRight().accept(getTraverser());
    getPrinter().print(", _Intersection_" + index + ")");
    operations += ", " + getPrinter().getContent();
    getPrinter().clearBuffer();
    getPrinter().print(temp);
    index++;
  }

  @Override
  public void handle(ASTSetEnumeration node) {
    for (int i = 0; i < node.sizeSetCollectionItems(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      node.getSetCollectionItem(i).accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTSetValueItem node) {
    for (int i = 0; i < node.sizeExpressions(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      node.getExpression(i).accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTSetValueRange node) {
    getPrinter().print("_Range_" + index);
    String temp = getPrinter().getContent();
    getPrinter().clearBuffer();
    getPrinter().print("numlist(");
    node.getLowerBound().accept(getTraverser());
    getPrinter().print(",");
    node.getUpperBound().accept(getTraverser());
    getPrinter().print(",_Range_" + index + ")");
    operations += ", " + getPrinter().getContent();
    getPrinter().clearBuffer();
    getPrinter().print(temp);
    index++;
  }
}
