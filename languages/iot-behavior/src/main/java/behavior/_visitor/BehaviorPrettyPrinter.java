package behavior._visitor;

import behavior._ast.ASTAfterStatement;
import behavior._ast.ASTEveryBlock;
import behavior._ast.ASTLogStatement;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class BehaviorPrettyPrinter implements BehaviorVisitor {

  protected BehaviorVisitor realThis = this;
  protected IndentPrinter printer;

  public BehaviorPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public BehaviorPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public BehaviorVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull BehaviorVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle (ASTAfterStatement node){
    getPrinter().print("after ");
    node.getSIUnitLiteral().accept(getRealThis());
    getPrinter().print(" ");
    node.getMCJavaBlock().accept(getRealThis());
  }

  @Override
  public void handle (ASTEveryBlock node){
    if(node.isPresentName()){
      getPrinter().print(node.getName() + ": ");
    }
    getPrinter().print("every ");
    node.getSIUnitLiteral().accept(getRealThis());
    getPrinter().print(" ");
    node.getMCJavaBlock().accept(getRealThis());
  }

  @Override
  public void handle (ASTLogStatement node){
    getPrinter().print("log ");
    node.getStringLiteral().accept(getRealThis());
  }
}
