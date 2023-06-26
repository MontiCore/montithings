// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;


import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.StringTransformations;
import montithings.generator.helper.TypesPrinter;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import sdformttest._ast.ASTCompareOperator;
import sdformttest._ast.ASTExpectValueOnPort;
import sdformttest._ast.ASTSendValueOnPort;
import sdformttest._ast.ASTTestBlock;
import sdformttest._visitor.SDForMTTestHandler;
import sdformttest._visitor.SDForMTTestTraverser;

import java.util.List;
import java.util.Optional;


public class CppSDForMTTestPrettyPrinter
        implements SDForMTTestHandler {

  protected SDForMTTestTraverser traverser;
  protected IndentPrinter printer;

  private String portName;
  final private MontiThingsTypeCheck tc;

  public CppSDForMTTestPrettyPrinter(IndentPrinter printer, String portName) {
    this.printer = printer;
    this.portName = portName;
    tc = new MontiThingsTypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());
  }

  public void setPortName(String portName) {
    this.portName = portName;
  }

  @Override
  public void handle(ASTTestBlock node) {
    List<ASTSendValueOnPort> sendValueOnPortList = node.getSendValueOnPortList();
    List<ASTExpectValueOnPort> expectValueOnPortList = node.getExpectValueOnPortList();
    ComponentTypeSymbol componentTypeSymbol = (ComponentTypeSymbol) node.getEnclosingScope().getSpanningSymbol();
    Optional<PortSymbol> ps = componentTypeSymbol.getPort(portName);
    for (ASTSendValueOnPort out : sendValueOnPortList) {
      getPrinter().print("component.getMqttClientSenderInstance" + portName
              + "()->publish (replaceDotsBySlashes (\"/connectors/\" + ");
      printGetExternalPortAccessFQN(out.getName());
      getPrinter().print("), replaceDotsBySlashes (");
      getPrinter().print("instanceName");
      getPrinter().print(" + \"/" + "test__" + out.getName() + "\"");
      getPrinter().println("));");

      getPrinter().print("component.getMqttClientInstance()->subscribe(replaceDotsBySlashes (\"/ports/\" + ");
      getPrinter().println("instanceName + \"/test__" + out.getName() + "\"));");
      getPrinter().print("component.getSubscriptionsToSend" + portName +
              "()->emplace(replaceDotsBySlashes (\"/ports/\" + ");
      getPrinter().println("instanceName + \"/test__" + out.getName() + "\"));");
    }
    for (ASTExpectValueOnPort in : expectValueOnPortList) {
      getPrinter().print("component.getMqttClientInstance()->publish (replaceDotsBySlashes (\"/connectors/\" + ");
      getPrinter().print("instanceName");
      getPrinter().print(" + \"/" + "test__" + in.getName() + "\"");
      getPrinter().print("), replaceDotsBySlashes (");
      printGetExternalPortAccessFQN(in.getName());
      getPrinter().println("));");

      if (ps.isPresent()) {
        getPrinter().print("component.getMqttClientSenderInstance" + portName
                + "()->publish(\"/new-subscriptions/" + ps.get().getType().print() + "\", replaceDotsBySlashes (");
        printGetExternalPortAccessFQN(in.getName());
        getPrinter().println("));");
      }
    }

    getPrinter().println("std::this_thread::sleep_for (std::chrono::milliseconds (1000));");

    for (ASTSendValueOnPort out : sendValueOnPortList) {
      SymTypeExpression type = tc.typeOf(out.getExpression());
      String typeName = TypesPrinter.printCPPTypeName(type);
      String messageName = out.getName() + "__message";
      getPrinter().println("Message<" + typeName + "> " + messageName + " = Message<" + typeName + ">();");
      getPrinter().println(messageName + ".setUuid(sole::uuid4());");
      getPrinter().print(messageName + ".setPayload(");
      out.getExpression().accept(getTraverser());
      getPrinter().println(");");
      getPrinter().print("interface.getPortTest__" + out.getName() + "()");
      getPrinter().println("->setNextValue(" + messageName + ");");
    }

    // Print wait statement
    getPrinter().print("auto end = std::chrono::high_resolution_clock::now() + std::chrono::");
    getPrinter().print(TypesPrinter.printTime(node.getWaitStatement().getSIUnitLiteral()));
    getPrinter().println(";");

    getPrinter().print("while (std::chrono::high_resolution_clock::now() <= end");
    for (ASTExpectValueOnPort in : expectValueOnPortList) {
      getPrinter().print(" && (!interface.getPortTest__" + in.getName() + "()->hasValue(uuid)");
      getPrinter().print(" || interface.getPortTest__" + in.getName() + "()->getCurrentValue(uuid)" +
        "->getPayload().value() ");
      final ASTCompareOperator compareOperator = in.getCompareOperator();
      final String comparisonInCode;
      if (compareOperator.isPresentEquals()) {
        comparisonInCode = "!=";
      } else if (compareOperator.isPresentNotEquals()) {
        comparisonInCode = "==";
      } else if (compareOperator.isPresentLessThan()) {
        comparisonInCode = ">=";
      } else if (compareOperator.isPresentGreaterThan()) {
        comparisonInCode = "<=";
      } else if (compareOperator.isPresentGreaterEquals()) {
        comparisonInCode = "<";
      } else {
        comparisonInCode = ">";
      }
      getPrinter().print(comparisonInCode + " ");
      in.getExpression().accept(getTraverser());
      getPrinter().print(")");
    }
    getPrinter().println(") {");
    getPrinter().println("std::this_thread::yield();");
    getPrinter().println("std::this_thread::sleep_for (std::chrono::milliseconds (10));");
    getPrinter().println("}");

    // Disconnect test ports
    for (ASTSendValueOnPort out : sendValueOnPortList) {
      getPrinter().print("component.getMqttClientInstance()->publish (replaceDotsBySlashes (\"/disconnect/\" + ");
      printGetExternalPortAccessFQN(out.getName());
      getPrinter().print("), replaceDotsBySlashes (");
      getPrinter().print("instanceName");
      getPrinter().print(" + \"/" + "test__" + out.getName() + "\"");
      getPrinter().println("));");
    }
    for (ASTExpectValueOnPort in : expectValueOnPortList) {
      getPrinter().print("component.getMqttClientInstance()->publish (replaceDotsBySlashes (\"/disconnect/\" + ");
      getPrinter().print("instanceName");
      getPrinter().print(" + \"/" + "test__" + in.getName() + "\"");
      getPrinter().print("), replaceDotsBySlashes (");
      printGetExternalPortAccessFQN(in.getName());
      getPrinter().println("));");
    }

    if (ps.isPresent()) {
      getPrinter().println("if (std::chrono::high_resolution_clock::now() > end) {");
      getPrinter().println("component.getMqttClientSenderInstance" + portName + "()->" +
              "publish(\"/connection-start/" + ps.get().getType().print() + "\", \"failure\");");
      getPrinter().println("} else {");
      getPrinter().println("component.setIsConnected" + portName + "();");
      getPrinter().println("component.getMqttClientSenderInstance" + portName + "()->" +
              "publish(\"/connection-start/" + ps.get().getType().print() + "\", \"success\");");
    }
  }


  protected void printGetExternalPortAccessFQN(String s) {
    getPrinter().print(
      "input.get" + StringTransformations.capitalize(portName) + " ().value ()" +
        ".get" + StringTransformations.capitalize(s) + " ()" +
        ".getFullyQualifiedName ()"
    );
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
