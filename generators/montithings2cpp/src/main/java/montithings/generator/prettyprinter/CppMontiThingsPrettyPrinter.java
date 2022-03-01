// (c) https://github.com/MontiCore/monticore
package montithings.generator.prettyprinter;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import montithings._ast.ASTIsPresentExpression;
import montithings._ast.ASTPublishPort;
import montithings._symboltable.IMontiThingsScope;
import montithings._visitor.MontiThingsPrettyPrinter;
import montithings._visitor.MontiThingsTraverser;
import montithings.generator.codegen.util.Identifier;
import montithings.generator.helper.ComponentHelper;
import portextensions._ast.ASTSyncStatement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montithings.generator.prettyprinter.CppPrettyPrinterUtils.*;
import static montithings.util.IdentifierUtils.getPortForName;

public class CppMontiThingsPrettyPrinter extends MontiThingsPrettyPrinter {
  protected MontiThingsTraverser traverser;

  public CppMontiThingsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override public void handle(ASTPublishPort node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    for (String port : node.getPublishedPortsList()) {
      getPrinter().print("interface.getPort" + capitalize(port) +
        "()->setNextValue(" + Identifier.getResultName() +
        ".get" + capitalize(port) + "Message());");
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }

  @Override
  public void handle(ASTIsPresentExpression node) {
    CommentPrettyPrinter.printPreComments(node, getPrinter());
    Optional<PortSymbol> port = getPortForName(node.getNameExpression());
    if (!port.isPresent()) {
      getTraverser().handle(node.getNameExpression());
      return;
    }

    IMontiThingsScope componentScope = getScopeOfEnclosingComponent(node);
    ComponentTypeSymbol comp = (ComponentTypeSymbol) componentScope.getSpanningSymbol();

    List<PortSymbol> portsInBatchStatement = ComponentHelper.getPortsInBatchStatement(comp);

    List<ASTSyncStatement> syncStatements = ComponentHelper
      .elementsOf(comp).filter(ASTSyncStatement.class::isInstance).map(ASTSyncStatement.class::cast)
      .collect(Collectors.toList());

    if (port.isPresent()) {
      String prefix;
      if (port.get().isIncoming()) {
        prefix = Identifier.getInputName();
      }
      else {
        prefix = Identifier.getResultName();
      }

      getPrinter().print(prefix + ".get" + capitalize(node.getNameExpression().getName()) + "()");

    }
    else if (!syncStatements.isEmpty()) {
      StringBuilder synced = new StringBuilder("(");
      for (ASTSyncStatement sync : syncStatements) {
        String s1 = sync
          .getSyncedPortList()
          .stream()
          .map(str -> Identifier.getInputName() + ".get" + capitalize(str) + "()" + isSet(
            portsInBatchStatement, str))
          .collect(Collectors.joining(" && "));
        synced.append(s1);
      }
      synced.append(")");
      getPrinter().print(synced.toString());

    }
    else {
      getPrinter().print(node.getNameExpression().getName());
    }
    CommentPrettyPrinter.printPostComments(node, getPrinter());
  }





  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  @Override public MontiThingsTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(MontiThingsTraverser traverser) {
    this.traverser = traverser;
  }
}
