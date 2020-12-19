// (c) https://github.com/MontiCore/monticore
package conditioncatch._visitor;

import com.google.common.base.Preconditions;
import conditioncatch._ast.ASTConditionCatch;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class ConditionCatchPrettyPrinter implements ConditionCatchVisitor {

  protected ConditionCatchVisitor realThis = this;
  protected IndentPrinter printer;

  public ConditionCatchPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public ConditionCatchPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public ConditionCatchVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull ConditionCatchVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTConditionCatch node){
    node.getCondition().accept(this.getRealThis());
    this.getPrinter().print(" catch ");
    node.getHandler().accept(this.getRealThis());
    this.getPrinter().println(";");
  }


}