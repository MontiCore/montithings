// (c) https://github.com/MontiCore/monticore
package prepostcondition._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;

public class PrePostConditionPrettyPrinter implements PrePostConditionHandler {

  protected PrePostConditionTraverser traverser;

  protected IndentPrinter printer;

  public PrePostConditionPrettyPrinter() {
    this.printer = new IndentPrinter();
  }

  public PrePostConditionPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public PrePostConditionTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull PrePostConditionTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTPrecondition node) {
    this.getPrinter().print("pre ");
    node.getGuard().accept(getTraverser());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTPostcondition node) {
    this.getPrinter().print("post ");
    node.getGuard().accept(getTraverser());
    this.getPrinter().println(";");
  }
}