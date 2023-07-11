// (c) https://github.com/MontiCore/monticore
package componenttest._visitor;

import com.google.common.base.Preconditions;
import componenttest._ast.*;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ComponentTestPrettyPrinter implements ComponentTestHandler {

  protected ComponentTestTraverser traverser;

  protected IndentPrinter printer;

  public ComponentTestPrettyPrinter() {
    this.printer = new IndentPrinter();
  }

  public ComponentTestPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ComponentTestTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull ComponentTestTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTTestBlock node) {
    getPrinter().println("test {");
    for (ASTSendValueOnPort sendValueOnPort : node.getSendValueOnPortList()) {
      sendValueOnPort.accept(getTraverser());
      getPrinter().println();
    }
    node.getWaitStatement().accept(getTraverser());
    for (ASTExpectValueOnPort expectValueOnPort : node.getExpectValueOnPortList()) {
      expectValueOnPort.accept(getTraverser());
      getPrinter().println();
    }
    getPrinter().println();
    getPrinter().println("}");
  }

  @Override
  public void handle(ASTSendValueOnPort node) {
    getPrinter().print(node.getName() + " = ");
    node.getExpression().accept(getTraverser());
    getPrinter().print(";");
  }

  @Override
  public void handle(ASTWaitStatement node) {
    getPrinter().println("wait ");
    node.getSIUnitLiteral().accept(getTraverser());
    getPrinter().print(";");
  }

  @Override
  public void handle(ASTExpectValueOnPort node) {
    getPrinter().print("assert " + node.getName() + " ");
    node.getCompareOperator().accept(getTraverser());
    getPrinter().print(" ");
    node.getExpression().accept(getTraverser());
    getPrinter().print(";");
  }

  @Override
  public void handle(ASTCompareOperator node) {
    if (node.isPresentEquals()) {
      getPrinter().print("=");
    } else if (node.isPresentNotEquals()) {
      getPrinter().print("!=");
    } else if (node.isPresentLessThan()) {
      getPrinter().print("<");
    } else if (node.isPresentGreaterThan()) {
      getPrinter().print(">");
    } else if (node.isPresentLessEquals()) {
      getPrinter().print("<=");
    } else if (node.isPresentGreaterEquals()) {
      getPrinter().print(">=");
    }
  }
}
