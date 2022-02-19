// (c) https://github.com/MontiCore/monticore
package clockcontrol._visitor;

import clockcontrol._ast.ASTCalculationInterval;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ClockControlPrettyPrinter implements ClockControlHandler {

  protected ClockControlTraverser traverser;
  protected IndentPrinter printer;

  public ClockControlPrettyPrinter() {
    this.printer = new IndentPrinter();
  }

  public ClockControlPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTCalculationInterval node){
    this.getPrinter().print("update interval ");
    node.getInterval().accept(getTraverser());
    this.getPrinter().println(";");
  }

  @Override public ClockControlTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(ClockControlTraverser traverser) {
    this.traverser = traverser;
  }
}