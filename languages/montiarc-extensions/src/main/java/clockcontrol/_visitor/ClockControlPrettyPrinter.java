// (c) https://github.com/MontiCore/monticore
package clockcontrol._visitor;

import clockcontrol._ast.ASTCalculationInterval;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ClockControlPrettyPrinter implements ClockControlVisitor {

  protected ClockControlVisitor realThis = this;
  protected IndentPrinter printer;

  public ClockControlPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public ClockControlPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ClockControlVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull ClockControlVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTCalculationInterval node){
    this.getPrinter().print("update interval ");
    node.getInterval().accept(this.getRealThis());
    switch (node.getTimeUnit()){
      case MSEC: this.getPrinter().print("msec"); break;
      case SEC: this.getPrinter().print("sec"); break;
      case MIN: this.getPrinter().print("min"); break;
    }
    this.getPrinter().println(";");
  }

}