// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import montithings.tools.sd4componenttesting._ast.ASTSD4CConnection;
import montithings.tools.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import montithings.tools.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;
import montithings.tools.sd4componenttesting.util.SD4CElementType;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;
import montithings.tools.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import montithings.tools.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;

import java.util.List;
import java.util.Optional;

public class SD4CConnectionPortsDirectionValid implements SD4ComponentTestingASTSD4CConnectionCoCo {
  @Override
  public void check(ASTSD4CConnection node) {
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    //Case 4: PORTACCESS -> PORTACCESS : VALUE;
    // will checked in case 2 or 3
    if (node.getType() != SD4CElementType.DEFAULT) {
      return;
    }

    // check source
    if (!node.getSource().isPresentComponent()) {
      Optional<PortSymbol> portSymbol = mainComponent.getIncomingPort(node.getSource().getPort());

      if (!portSymbol.isPresent()) {
        SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
        String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
        Log.error(String.format(
          SD4ComponentTestingError.MAIN_INPUT_UNKNOWN_PORT.toString(),
          node.getSource().getPort(),
          nodeString,
          mainComponent.getName()));
        return;
      }
    }

    if (node.getSource().isPresentComponent()) {
      Optional<ComponentInstanceSymbol> component = mainComponent.getSubComponent(node.getSource().getComponent());
      Optional<PortSymbol> portSymbol = component.get().getType().getOutgoingPort(node.getSource().getPort());

      if (!portSymbol.isPresent()) {
        SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
        String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
        Log.error(String.format(
          SD4ComponentTestingError.CONNECTION_SOURCE_UNKNOWN_PORT.toString(),
          node.getSource().getPort(),
          nodeString,
          node.getSource().getComponent()));
        return;
      }
    }

    // check targets
    List<ASTPortAccess> targetList = node.getTargetList();

    for (ASTPortAccess target: targetList) {

      if (!target.isPresentComponent()) {
        Optional<PortSymbol> portSymbol = mainComponent.getOutgoingPort(target.getPort());

        if (!portSymbol.isPresent()) {
          SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
          String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
          Log.error(String.format(
            SD4ComponentTestingError.MAIN_OUTPUT_UNKNOWN_PORT.toString(),
            target.getPort(),
            nodeString,
            mainComponent.getName()));
          return;
        }
      }

      if (target.isPresentComponent()) {
        Optional<ComponentInstanceSymbol> component = mainComponent.getSubComponent(target.getComponent());
        Optional<PortSymbol> portSymbol = component.get().getType().getIncomingPort(target.getPort());

        if (!portSymbol.isPresent()) {
          SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
          String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
          Log.error(String.format(
            SD4ComponentTestingError.CONNECTION_TARGET_UNKNOWN_PORT.toString(),
            target.getPort(),
            nodeString,
            target.getComponent()));
          return;
        }
      }
    }
  }
}
