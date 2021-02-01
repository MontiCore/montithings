// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._ast.ASTArcBasisNode;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montithings._ast.*;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

public class MontiThingsPrettyPrinter implements MontiThingsVisitor {

  protected MontiThingsVisitor realThis = this;

  protected IndentPrinter printer;

  public MontiThingsPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public MontiThingsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public MontiThingsVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull MontiThingsVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTArcBasisNode> void acceptSeperatedList(@NotNull List<T> list) {
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
  public void handle(@NotNull ASTMTComponentType node) {
    node.getMTComponentModifier().accept(this.getRealThis());
    this.getPrinter().print(node.getName());
    node.getHead().accept(this.getRealThis());
    acceptSeperatedList(node.getComponentInstanceList());
    node.getBody().accept(this.getRealThis());
  }

  @Override
  public void handle(@NotNull ASTMTComponentModifier node) {
    if (node.isInterface()) {
      this.getPrinter().print("interface ");
    }
    this.getPrinter().print("component ");
  }

  @Override
  public void handle(@NotNull ASTBehavior node) {
    this.getPrinter().print("behavior ");
    node.getMCJavaBlock().accept(this.getRealThis());
  }

  @Override
  public void handle(@NotNull ASTIsPresentExpression node) {
    node.getNameExpression().accept(this.getRealThis());
    this.getPrinter().print("?");
  }
}