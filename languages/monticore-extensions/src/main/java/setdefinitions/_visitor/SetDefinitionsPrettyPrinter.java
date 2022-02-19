// (c) https://github.com/MontiCore/monticore
package setdefinitions._visitor;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import setdefinitions._ast.ASTSetDefinitionsNode;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._ast.ASTSetValueRegEx;

import java.util.Iterator;
import java.util.List;

public class SetDefinitionsPrettyPrinter implements SetDefinitionsHandler {

  protected SetDefinitionsTraverser traverser;

  protected IndentPrinter printer;

  public SetDefinitionsPrettyPrinter() {
    this.printer = new IndentPrinter();
  }

  public SetDefinitionsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public SetDefinitionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull SetDefinitionsTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTExpressionsBasisNode> void acceptSeperatedList(@NotNull List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(getTraverser());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(getTraverser());
    }
  }

  public <T extends ASTSetDefinitionsNode> void acceptSeperatedSetList(@NotNull List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(getTraverser());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTSetValueRange node) {
    node.getLowerBound().accept(getTraverser());
    if (node.isPresentStepsize()) {
      this.getPrinter().print(":");
      node.getStepsize().accept(getTraverser());
    }
    this.getPrinter().print(":");
    node.getUpperBound().accept(getTraverser());
  }

  @Override
  public void handle(ASTSetValueRegEx node) {
    this.getPrinter().print("format :");
    node.getFormat().accept(getTraverser());
  }
}