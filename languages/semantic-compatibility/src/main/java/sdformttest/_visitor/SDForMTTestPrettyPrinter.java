// (c) https://github.com/MontiCore/monticore
package sdformttest._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import sdformttest._ast.*;

public class SDForMTTestPrettyPrinter implements SDForMTTestHandler {

  protected SDForMTTestTraverser traverser;

  protected IndentPrinter printer;

  public SDForMTTestPrettyPrinter() {
    this.printer = new IndentPrinter();
  }

  public SDForMTTestPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public SDForMTTestTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull SDForMTTestTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  /*@Override
  public void handle(ASTAfterStatement node) {
    getPrinter().print("after");
    node.getTimeSpan().accept(getTraverser());
  }

  @Override
  public void handle(ASTConcreteTimeSpan node) {
    node.getSIUnitLiteral().accept(getTraverser());
  }

  @Override
  public void handle(ASTTimeSpanSet node) {
    getPrinter().print("[");
    node.getFrom().accept(getTraverser());
    getPrinter().print(", ");
    node.getTo().accept(getTraverser());
    getPrinter().print("]");
  }*/
}
