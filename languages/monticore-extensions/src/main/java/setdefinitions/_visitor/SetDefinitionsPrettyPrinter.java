// (c) https://github.com/MontiCore/monticore
package setdefinitions._visitor;

import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import setdefinitions._ast.*;

import java.util.Iterator;
import java.util.List;

public class SetDefinitionsPrettyPrinter implements SetDefinitionsVisitor {

  protected SetDefinitionsVisitor realThis = this;
  protected IndentPrinter printer;

  public SetDefinitionsPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public SetDefinitionsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public SetDefinitionsVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull SetDefinitionsVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTExpressionsBasisNode> void acceptSeperatedList(@NotNull List<T> list){
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getRealThis());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getRealThis());
    }
  }

  public <T extends ASTSetDefinitionsNode> void acceptSeperatedSetList(@NotNull List<T> list){
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getRealThis());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(ASTSetValueList node){
    acceptSeperatedList(node.getExpressionList());
  }

  @Override
  public void handle(ASTSetValueRange node){
    node.getLowerBound().accept(this.getRealThis());
    if(node.isPresentStepsize()){
      this.getPrinter().print(":");
      node.getStepsize().accept(this.getRealThis());
    }
    this.getPrinter().print(":");
    node.getUpperBound().accept(this.getRealThis());
  }

  @Override
  public void visit(ASTSetValueRegEx node){
    this.getPrinter().print("format :");
  }

  @Override
  public void handle(ASTSetDefinition node){
    this.getPrinter().println(" { ");
    this.getPrinter().indent();
    acceptSeperatedSetList(node.getSetAllowedValuesList());
    this.getPrinter().unindent();
    this.getPrinter().println(" }");
  }

}