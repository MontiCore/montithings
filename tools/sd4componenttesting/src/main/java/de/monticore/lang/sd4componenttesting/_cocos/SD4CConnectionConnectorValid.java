// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CConnection;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class SD4CConnectionConnectorValid implements SD4ComponentTestingASTSD4CConnectionCoCo {
  @Override
  public void check(ASTSD4CConnection node) {
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    //Case 4: PORTACCESS -> PORTACCESS : VALUE;

    // check source
    if (!node.getSource().isPresentComponent()) {
      Optional<PortSymbol> portSymbol = mainComponent.getIncomingPort(node.getSource().getPort());

      if (!portSymbol.isPresent()) {
        Log.error(String.format(
          SD4ComponentTestingError.MAIN_INPUT_UNKNOWN_PORT.toString(),
          node.getSource().getPort(),
          node,
          mainComponent.getName()));
        return;
      }
    }

    if (node.getSource().isPresentComponent()) {
      Optional<ComponentInstanceSymbol> component = mainComponent.getSubComponent(node.getSource().getComponent());
      Optional<PortSymbol> portSymbol = component.get().getType().getOutgoingPort(node.getSource().getPort());

      if (!portSymbol.isPresent()) {
        Log.error(String.format(
          SD4ComponentTestingError.CONNECTION_SOURCE_UNKNOWN_PORT.toString(),
          node.getSource().getComponent(),
          node,
          node.getSource()));
        return;
      }
    }

    // check targets
    List<ASTPortAccess> targetList = node.getTargetList();

    for (ASTPortAccess target: targetList) {

      if (!target.isPresentComponent()) {
        Optional<PortSymbol> portSymbol = mainComponent.getIncomingPort(target.getPort());

        if (!portSymbol.isPresent()) {
          Log.error(String.format(
            SD4ComponentTestingError.MAIN_OUTPUT_UNKNOWN_PORT.toString(),
            target.getPort(),
            node,
            mainComponent.getName()));
          return;
        }
      }

      if (target.isPresentComponent()) {
        Optional<ComponentInstanceSymbol> component = mainComponent.getSubComponent(target.getComponent());
        Optional<PortSymbol> portSymbol = component.get().getType().getOutgoingPort(target.getPort());

        if (!portSymbol.isPresent()) {
          Log.error(String.format(
            SD4ComponentTestingError.CONNECTION_TARGET_UNKNOWN_PORT.toString(),
            target.getComponent(),
            node,
            target));
          return;
        }
      }
    }
  }
}
