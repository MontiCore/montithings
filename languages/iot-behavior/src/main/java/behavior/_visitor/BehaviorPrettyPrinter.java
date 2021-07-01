// (c) https://github.com/MontiCore/monticore
package behavior._visitor;

import behavior._ast.ASTAfterStatement;
import behavior._ast.ASTAgoQualification;
import behavior._ast.ASTEveryBlock;
import behavior._ast.ASTLogStatement;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

public class BehaviorPrettyPrinter implements BehaviorHandler {

  protected BehaviorTraverser traverser;

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
  public BehaviorTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull BehaviorTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  @Override
  public void handle(ASTAfterStatement node) {
    getPrinter().print("after ");
    node.getSIUnitLiteral().accept(getTraverser());
    getPrinter().print(" ");
    node.getMCJavaBlock().accept(getTraverser());
  }

  @Override
  public void handle(ASTEveryBlock node) {
    if (node.isPresentName()) {
      getPrinter().print(node.getName() + ": ");
    }
    getPrinter().print("every ");
    node.getSIUnitLiteral().accept(getTraverser());
    getPrinter().print(" ");
    node.getMCJavaBlock().accept(getTraverser());
  }

  @Override
  public void handle(ASTLogStatement node) {
    getPrinter().print("log ");
    node.getStringLiteral().accept(getTraverser());
    getPrinter().println(";");
  }

  @Override
  public void handle(ASTAgoQualification node) {
    node.getExpression().accept(getTraverser());
    getPrinter().print("@ago");
    getPrinter().print("(");
    node.getSIUnitLiteral().accept(getTraverser());
    getPrinter().print(")");
  }
}
