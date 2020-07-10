/* (c) https://github.com/MontiCore/monticore */
package prepostcondition._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;

public class PrePostConditionPrettyPrinter implements PrePostConditionVisitor {

  private PrePostConditionVisitor realThis = this;
  protected IndentPrinter printer;

  public PrePostConditionPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public PrePostConditionPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public PrePostConditionVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull PrePostConditionVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTPrecondition node){
    this.getPrinter().print("pre ");
    node.getGuard().accept(this.getRealThis());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTPostcondition node){
    this.getPrinter().print("post ");
    node.getGuard().accept(this.getRealThis());
    this.getPrinter().println(";");
  }
}