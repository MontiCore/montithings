package behavior._visitor;

import behavior._ast.ASTAfterStatement;
import behavior._ast.ASTBehaviorNode;
import behavior._ast.ASTEveryStatement;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

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

  public <T extends ASTExpressionsBasisNode> void acceptSeperatedList(@NotNull List<T> list){
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getRealThis());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getRealThis());
    }
  }

  public <T extends ASTBehaviorNode> void acceptSeperatedSetList(@NotNull List<T> list){
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getRealThis());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getRealThis());
    }
  }

  @Override
  public void handle (ASTAfterStatement node){
    getPrinter().print("after ");
    node.getNatLiteral().accept(getRealThis());
    getPrinter().print(" ");
    if(node.isPresentSeconds()){
      getPrinter().print("sec");
    } else if (node.isPresentMilliseconds()){
      getPrinter().print("msec");
    } else {
      getPrinter().print("min");
    }
    getPrinter().print(" ");
    node.getMCJavaBlock().accept(getRealThis());
  }

  @Override
  public void handle (ASTEveryStatement node){
    getPrinter().print("after ");
    node.getNatLiteral().accept(getRealThis());
    getPrinter().print(" ");
    if(node.isPresentSeconds()){
      getPrinter().print("sec");
    } else if (node.isPresentMilliseconds()){
      getPrinter().print("msec");
    } else {
      getPrinter().print("min");
    }
    getPrinter().print(" ");
    node.getMCJavaBlock().accept(getRealThis());
  }
}
