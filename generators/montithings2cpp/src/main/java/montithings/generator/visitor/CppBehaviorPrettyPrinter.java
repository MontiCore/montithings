package montithings.generator.visitor;

import behavior._ast.ASTAfterStatement;
import behavior._ast.ASTLogStatement;
import behavior._visitor.BehaviorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import montithings._auxiliary.ExpressionsBasisMillForMontiThings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    printTime(node.getSIUnitLiteral());
    getPrinter().print(");");

    node.getMCJavaBlock().accept(getRealThis());

    getPrinter().print("return true;");
    getPrinter().print("} );");
  }

  @Override
  public void handle (ASTLogStatement node) {
    getPrinter().print("LOG(INFO) <<");
    printLogString(node);
    getPrinter().print(";");
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

  private void printTime(ASTSIUnitLiteral lit){
    if(SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("ns")){
      getPrinter().print("nanoseconds");
    }
    else if(SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("μs")){
      getPrinter().print("microseconds");
    }
    else if(SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("ms")){
      getPrinter().print("milliseconds");
    }
    else if(SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("s")){
      getPrinter().print("seconds");
    }
    else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("min")) {
      getPrinter().print("minutes");
    }
    getPrinter().print("{");
    lit.getNumericLiteral().accept(getRealThis());
    getPrinter().print("}");
  }

  protected void printLogString(ASTLogStatement node) {
    String input = node.getStringLiteral().getSource();
    Matcher m = Pattern.compile("\\$\\w+").matcher(input);
    int currentPosition = 0;
    while (m.find()) {
      getPrinter().print("\"");
      getPrinter().print(input.substring(currentPosition, m.start()));
      getPrinter().print("\"");

      getPrinter().print(" << ");
      ASTNameExpression name = ExpressionsBasisMillForMontiThings
        .nameExpressionBuilder()
        .setName(input.substring(m.start() + 1, m.end()))
        .build();
      name.setEnclosingScope(node.getEnclosingScope());
      name.accept(getRealThis());
      getPrinter().print(" << ");

      currentPosition = m.end();
    }

    getPrinter().print("\"");
    getPrinter().print(input.substring(currentPosition));
    getPrinter().print("\"");
  }
}
