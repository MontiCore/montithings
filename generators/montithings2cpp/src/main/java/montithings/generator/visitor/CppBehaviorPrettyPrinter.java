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
import de.se_rwth.commons.logging.Log;
import montithings._auxiliary.ExpressionsBasisMillForMontiThings;
import montithings.generator.codegen.util.Identifier;

import java.util.Optional;

import static montithings.generator.visitor.CppPrettyPrinterUtils.capitalize;
import static montithings.util.IdentifierUtils.getPortForName;

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
  public void handle (ASTLogStatement node){
    getPrinter().print("LOG(INFO) ");
    printLogString(node.getStringLiteral().getValue(), node);
    getPrinter().print(";");
  }

  @Override
  public void handle (ASTAgoQualification node){
    if(node.getExpression() instanceof ASTNameExpression){
      ASTNameExpression name = (ASTNameExpression) node.getExpression();
      Optional<PortSymbol> port = getPortForName(name);
      if(port.isPresent()){
        //TODO: port
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
      //TODO: handle cases where expression is not a NameExpression
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
    else if(SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("min")){
      getPrinter().print("minutes");
    }
    getPrinter().print("{");
    lit.getNumericLiteral().accept(getRealThis());
    getPrinter().print("}");
  }

  private void printLogString(String s, ASTLogStatement node){
    if(s.length() > 0){
      getPrinter().print("<< ");
      int index = s.indexOf('$');
      if(index == -1){
        getPrinter().print("\"");
        getPrinter().print(s);
        getPrinter().print("\"");
      }
      else {
        if(index == s.length() - 1){
          Log.error("Char \'$\' cannot appear without a variable name behind it");
        }
        else {
          if(index != 0){
            getPrinter().print("\"");
            getPrinter().print(s.substring(0, index));
            getPrinter().print("\"");
            getPrinter().print(" << ");
          }
          int varEnd = s.indexOf(' ', index + 1);
          if(varEnd == -1){
            ASTNameExpression name = ExpressionsBasisMillForMontiThings
                    .nameExpressionBuilder()
                    .setName(s.substring(index + 1))
                    .build();
            name.setEnclosingScope(node.getEnclosingScope());
            name.accept(getRealThis());
          }
          else {
            ASTNameExpression name = ExpressionsBasisMillForMontiThings
                    .nameExpressionBuilder()
                    .setName(s.substring(index + 1, varEnd))
                    .build();
            name.setEnclosingScope(node.getEnclosingScope());
            name.accept(getRealThis());
            printLogString(s.substring(varEnd), node);
          }
        }
      }
    }
  }
}
