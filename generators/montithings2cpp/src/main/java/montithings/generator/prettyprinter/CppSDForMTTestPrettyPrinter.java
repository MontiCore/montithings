// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;


import arcbasis._ast.ASTConnector;
import behavior._ast.ASTConnectStatement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import montithings.MontiThingsMill;
import sdformttest._ast.ASTExpectValueOnPort;
import sdformttest._ast.ASTSendValueOnPort;
import sdformttest._ast.ASTTestBlock;
import sdformttest._visitor.SDForMTTestHandler;
import sdformttest._visitor.SDForMTTestTraverser;


public class CppSDForMTTestPrettyPrinter
        implements SDForMTTestHandler {

  protected SDForMTTestTraverser traverser;
  protected IndentPrinter printer;

  public CppSDForMTTestPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTTestBlock node) {
    for (ASTSendValueOnPort out : node.getSendValueOnPortList()) {
      ASTConnector connector = MontiThingsMill.connectorBuilder()
        .setSource("test__" + out.getName())
        .addTarget("connect." + out.getName()).build(); // TODO statt connect korrekten Namen des Ports
      ASTConnectStatement connectStatement = MontiThingsMill.connectStatementBuilder().setConnector(connector).build();
      connectStatement.accept(getTraverser());
    }
    for (ASTExpectValueOnPort in : node.getExpectValueOnPortList()) {
      ASTConnector connector = MontiThingsMill.connectorBuilder()
        .setSource("connect." + in.getName()) // TODO statt connect korrekten Namen des Ports
        .addTarget("test__" + in.getName()).build();
      ASTConnectStatement connectStatement = MontiThingsMill.connectStatementBuilder().setConnector(connector).build();
      connectStatement.accept(getTraverser());
    }
  }

  protected void printTime(ASTSIUnitLiteral lit) {
    if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("ns")) {
      getPrinter().print("nanoseconds");
    } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("μs")) {
      getPrinter().print("microseconds");
    } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("ms")) {
      getPrinter().print("milliseconds");
    } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("s")) {
      getPrinter().print("seconds");
    } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("min")) {
      getPrinter().print("minutes");
    }
    getPrinter().print("{");
    lit.getNumericLiteral().accept(getTraverser());
    getPrinter().print("}");
  }



  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  protected IndentPrinter getPrinter() {
    return printer;
  }

  @Override
  public SDForMTTestTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(SDForMTTestTraverser traverser) {
    this.traverser = traverser;
  }
}
