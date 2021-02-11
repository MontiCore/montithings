package montithings.generator.visitor;

import behavior._ast.ASTAfterStatement;
import behavior._visitor.BehaviorVisitor;
import de.monticore.prettyprint.IndentPrinter;

public class CppBehaviorPrettyPrinter implements BehaviorVisitor {
  private BehaviorVisitor realThis;
  private IndentPrinter printer;

  public CppBehaviorPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.realThis = this;
  }

  @Override
  public void handle(ASTAfterStatement node){
    getPrinter().print("std::future<bool> fut = std::async(std::launch::async, [=] () -> bool {");
    getPrinter().print("std::this_thread::sleep_for( std::chrono::");
    getPrinter().print("seconds" + "{");
    node.getSIUnitLiteral().getNumericLiteral().accept(getRealThis());
    getPrinter().print("});");

    node.getMCJavaBlock().accept(getRealThis());

    getPrinter().print("return true;");
    getPrinter().print("} );");
  }

  private IndentPrinter getPrinter() {
    return printer;
  }

  @Override
  public BehaviorVisitor getRealThis(){
    return this.realThis;
  }

  @Override
  public void setRealThis(BehaviorVisitor realThis){
    this.realThis = realThis;
  }
}
