// (c) https://github.com/MontiCore/monticore
package conditionbasis._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ConditionBasisPrettyPrinter implements ConditionBasisHandler {

  protected ConditionBasisTraverser traverser;

  protected IndentPrinter printer;

  public ConditionBasisPrettyPrinter() {
    this.printer = new IndentPrinter();
  }

  public ConditionBasisPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ConditionBasisTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull ConditionBasisTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

}