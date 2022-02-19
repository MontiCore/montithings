// (c) https://github.com/MontiCore/monticore
package conditioncatch._visitor;

import com.google.common.base.Preconditions;
import conditionbasis._visitor.ConditionBasisTraverser;
import conditioncatch._ast.ASTConditionCatch;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ConditionCatchPrettyPrinter implements ConditionCatchHandler {

  protected ConditionCatchTraverser traverser;

  protected IndentPrinter printer;

  public ConditionCatchPrettyPrinter() {
     this.printer = new IndentPrinter();
  }

  public ConditionCatchPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ConditionCatchTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull ConditionCatchTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTConditionCatch node) {
    node.getCondition().accept((ConditionBasisTraverser) getTraverser());
    this.getPrinter().print(" catch ");
    node.getHandler().accept(getTraverser());
  }

}