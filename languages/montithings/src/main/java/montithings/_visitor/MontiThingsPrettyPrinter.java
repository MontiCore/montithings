// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._ast.ASTArcBasisNode;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import montithings._ast.*;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

public class MontiThingsPrettyPrinter implements MontiThingsHandler {

  protected MontiThingsTraverser traverser;

  protected IndentPrinter printer;

  public MontiThingsPrettyPrinter() {
    this.printer = new IndentPrinter();
  }

  public MontiThingsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public MontiThingsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull MontiThingsTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTArcBasisNode> void acceptSeperatedList(@NotNull List<T> list) {
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
  public void handle(@NotNull ASTMTComponentType node) {
    node.getMTComponentModifier().accept(getTraverser());
    this.getPrinter().print(node.getName());
    node.getHead().accept(getTraverser());
    if (node.isPresentMTImplements()) {
      node.getMTImplements().accept(getTraverser());
    }
    acceptSeperatedList(node.getComponentInstanceList());
    node.getBody().accept(getTraverser());
  }

  @Override
  public void handle(@NotNull ASTMTComponentModifier node) {
    if (node.isInterface()) {
      this.getPrinter().print("interface ");
    }
    this.getPrinter().print("component ");
  }

  @Override
  public void handle(@NotNull ASTMTImplements node) {
    this.getPrinter().print(" implements");
    for (int i = 0; i < node.getNameList().size(); i++) {
      if (i != 0) {
        getPrinter().print(",");
      }
      getPrinter().print(" ");
      getPrinter().print(node.getName(i));
    }
  }

  @Override
  public void handle(@NotNull ASTBehavior node) {
    this.getPrinter().print("behavior");
    List<String> ports = node.getNameList();
    for (int i = 0; i < ports.size(); i++) {
      if (i != 0) {
        getPrinter().print(",");
      }
      getPrinter().print(" ");
      getPrinter().print(ports.get(i));
    }
    getPrinter().print(" ");
    node.getMCJavaBlock().accept(getTraverser());
  }

  @Override
  public void handle(@NotNull ASTIsPresentExpression node) {
    node.getNameExpression().accept(getTraverser());
    this.getPrinter().print("?");
  }

  @Override public void handle(ASTPublishPort node) {
    getPrinter().print("publish ");
    List<String> ports = node.getPublishedPortsList();
    for (int i = 0; i < ports.size(); i++) {
      if (i != 0) {
        getPrinter().print(", ");
      }
      getPrinter().print(ports.get(i));
    }
    getPrinter().print(";");
  }
}