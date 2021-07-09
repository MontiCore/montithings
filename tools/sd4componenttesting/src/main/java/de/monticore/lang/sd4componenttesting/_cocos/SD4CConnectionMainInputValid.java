// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CConnection;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import de.monticore.lang.sd4componenttesting.util.SD4CElementType;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Optional;

public class SD4CConnectionMainInputValid implements SD4ComponentTestingASTSD4CConnectionCoCo {
  @Override
  public void check(ASTSD4CConnection node) {
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    //Case 3: -> PORTACCESS : VALUE;
    // will checked in case 2 or 4
    if (node.getType() != SD4CElementType.MAIN_INPUT) {
      return;
    }

    List<ASTPortAccess> targetList = node.getTargetList();

    // check if one target defines a component
    for (ASTPortAccess target : targetList) {
      if (target.isPresentComponent()) {
        Log.error(String.format(
          SD4ComponentTestingError.MAIN_INPUT_COMPONENT_GIVEN.toString(),
          target.getComponent(),
          node));
        return;
      }
    }

    // check if one target defines port which is not a incoming port of main component
    for (ASTPortAccess target : targetList) {
      Optional<PortSymbol> portSymbol = mainComponent.getIncomingPort(target.getPort());

      if (!portSymbol.isPresent()) {
        Log.error(String.format(
          SD4ComponentTestingError.MAIN_INPUT_UNKNOWN_PORT.toString(),
          target.getPort(),
          node,
          mainComponent.getName()));
      }
    }
  }
}
