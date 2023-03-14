// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;


import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import behavior._ast.ASTConnectStatement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.se_rwth.commons.StringTransformations;
import montithings.MontiThingsMill;
import sdformttest._ast.ASTExpectValueOnPort;
import sdformttest._ast.ASTSendValueOnPort;
import sdformttest._ast.ASTTestBlock;
import sdformttest._visitor.SDForMTTestHandler;
import sdformttest._visitor.SDForMTTestTraverser;

import java.util.List;


public class CppSDForMTTestPrettyPrinter
        implements SDForMTTestHandler {

  protected SDForMTTestTraverser traverser;
  protected IndentPrinter printer;

  public CppSDForMTTestPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTTestBlock node) {
    List<ASTSendValueOnPort> sendValueOnPortList = node.getSendValueOnPortList();
    List<ASTExpectValueOnPort> expectValueOnPortList = node.getExpectValueOnPortList();
    for (ASTSendValueOnPort out : sendValueOnPortList) {
      getPrinter().print("component.getMqttClientInstance()->publish (replaceDotsBySlashes (\"/connectors/\" + ");
      printGetExternalPortAccessFQN(out.getName());
      getPrinter().print("), replaceDotsBySlashes (");
      getPrinter().print("instanceName");
      getPrinter().print(" + \"/" + "test__" + out.getName() + "\"");
      getPrinter().print("));");
    }
    for (ASTExpectValueOnPort in : expectValueOnPortList) {
      getPrinter().print("component.getMqttClientInstance()->publish (replaceDotsBySlashes (\"/connectors/\" + ");
      getPrinter().print("instanceName");
      getPrinter().print(" + \"/" + "test__" + in.getName() + "\"");
      getPrinter().print("), replaceDotsBySlashes (");
      printGetExternalPortAccessFQN(in.getName());
      getPrinter().print("));");
    }

    getPrinter().println("std::this_thread::sleep_for (std::chrono::milliseconds (1000));");

    for (ASTSendValueOnPort out : sendValueOnPortList) {
      getPrinter().println("Message<int> message = Message<int>();"); // TODO Type dynamisch
      getPrinter().println("message.setUuid(sole::uuid4());");
      getPrinter().print("message.setPayload(");
      out.getExpression().accept(getTraverser());
      getPrinter().println(");");
      getPrinter().print("interface.getPortTest__" + out.getName() + "()");
      getPrinter().println("->setNextValue(message);");
    }

    getPrinter().println("auto end = std::chrono::high_resolution_clock::now()\n" +
      "+ std::chrono::seconds(5);"); // TODO WaitStatement

    for (ASTExpectValueOnPort in : expectValueOnPortList) {
      getPrinter().print("while (std::chrono::high_resolution_clock::now() <= end && !interface.getPortTest__");
      getPrinter().println(in.getName() + "()->hasValue(uuid)) {");
      getPrinter().println("std::this_thread::yield();");
      getPrinter().println("std::this_thread::sleep_for (std::chrono::milliseconds (10));");
      getPrinter().println("}");
    }

    // Disconnect test ports
    for (ASTSendValueOnPort out : sendValueOnPortList) {
      getPrinter().print("component.getMqttClientInstance()->publish (replaceDotsBySlashes (\"/disconnect/\" + ");
      printGetExternalPortAccessFQN(out.getName());
      getPrinter().print("), replaceDotsBySlashes (");
      getPrinter().print("instanceName");
      getPrinter().print(" + \"/" + "test__" + out.getName() + "\"");
      getPrinter().print("));");
    }
    for (ASTExpectValueOnPort in : expectValueOnPortList) {
      getPrinter().print("component.getMqttClientInstance()->publish (replaceDotsBySlashes (\"/disconnect/\" + ");
      getPrinter().print("instanceName");
      getPrinter().print(" + \"/" + "test__" + in.getName() + "\"");
      getPrinter().print("), replaceDotsBySlashes (");
      printGetExternalPortAccessFQN(in.getName());
      getPrinter().print("));");
    }

    getPrinter().println("if (std::chrono::high_resolution_clock::now() <= end) {");
  }


  protected void printGetExternalPortAccessFQN(String s) { // TODO weg von connect
    getPrinter().print(
      "input.get" + "Connect" + " ().value ()" +
        ".get" + StringTransformations.capitalize(s) + " ()" +
        ".getFullyQualifiedName ()"
    );
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
