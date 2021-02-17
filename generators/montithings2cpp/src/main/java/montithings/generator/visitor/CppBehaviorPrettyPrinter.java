package montithings.generator.visitor;

import behavior._ast.ASTAfterStatement;
import behavior._visitor.BehaviorVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.monticore.siunits.utility.Converter;
import de.monticore.siunits.utility.UnitFactory;

import javax.measure.unit.Unit;

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
    SIUnitLiteralDecoder decoder = new SIUnitLiteralDecoder();
    double valueInSeconds = decoder.getValue(lit);
    getPrinter().print("{");
    getPrinter().print((int) valueInSeconds);
    getPrinter().print("}");
  }
}
