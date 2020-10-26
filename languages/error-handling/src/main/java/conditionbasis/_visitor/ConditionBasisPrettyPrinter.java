// (c) https://github.com/MontiCore/monticore
package conditionbasis._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ConditionBasisPrettyPrinter implements ConditionBasisVisitor {

  private ConditionBasisVisitor realThis = this;
  protected IndentPrinter printer;

  public ConditionBasisPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public ConditionBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ConditionBasisVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull ConditionBasisVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }


}