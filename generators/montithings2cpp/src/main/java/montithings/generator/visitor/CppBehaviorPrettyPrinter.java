// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.PortSymbol;
import behavior._ast.ASTAfterStatement;
import behavior._ast.ASTAgoQualification;
import behavior._ast.ASTLogStatement;
import behavior._visitor.BehaviorVisitor;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import montithings._auxiliary.ExpressionsBasisMillForMontiThings;
import montithings.generator.codegen.util.Identifier;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static montithings.generator.visitor.CppPrettyPrinterUtils.capitalize;
import static montithings.util.IdentifierUtils.getPortForName;

public class CppBehaviorPrettyPrinter implements BehaviorVisitor {
  private BehaviorVisitor realThis;
  private IndentPrinter printer;
  private int afterStatementIndex;

  public CppBehaviorPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
    this.realThis = this;
    this.afterStatementIndex = 0;
  }

  @Override
  public void handle(ASTAfterStatement node){
    getPrinter().print("std::future<bool> fut");
    getPrinter().print(afterStatementIndex);
    getPrinter().print(" = std::async(std::launch::async, [=] () -> bool {");
    getPrinter().print("std::this_thread::sleep_for( std::chrono::");
    printTime(node.getSIUnitLiteral());
    getPrinter().print(");");

    node.getMCJavaBlock().accept(getRealThis());

    getPrinter().print("return true;");
    getPrinter().print("} );");

    this.afterStatementIndex++;
  }

  @Override
  public void handle (ASTLogStatement node) {
    getPrinter().print("LOG(INFO) <<");
    printLogString(node);
    getPrinter().print(";");
  }

  @Override
  public void handle (ASTAgoQualification node){
    handle(node, false);
  }

  public void handle (ASTAgoQualification node, boolean isComparedToNoData){
    if(node.getExpression() instanceof ASTNameExpression){
      ASTNameExpression name = (ASTNameExpression) node.getExpression();
      Optional<PortSymbol> port = getPortForName(name);
      if(port.isPresent()){
        if(port.get().isIncoming()){
          getPrinter().print(Identifier.getInputName());
        }
        else  {
          getPrinter().print(Identifier.getResultName());
        }
        getPrinter().print(".agoGet");
        getPrinter().print(capitalize(name.getName()) + "(std::chrono::");
        printTime(node.getSIUnitLiteral());
        getPrinter().print(")");
        if(!isComparedToNoData){
          getPrinter().print(".value()");
        }
      }
      else {
        Optional<VariableSymbol> symbol = node.getEnclosingScope().resolveVariable(name.getName());
        if(symbol.isPresent()){
          getPrinter().print(Identifier.getStateName() + ".agoGet");
          getPrinter().print(capitalize(name.getName()) + "(std::chrono::");
          printTime(node.getSIUnitLiteral());
          getPrinter().print(")");
        }
        else {
          getPrinter().print(name.getName());
        }
      }
    }
    else {
      node.getExpression().accept(getRealThis());
    }
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

      String subString = input.substring(m.start() + 1, m.end());
      ASTNameExpression name = ExpressionsBasisMillForMontiThings
              .nameExpressionBuilder()
              .setName(subString)
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
