// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._ast.ASTPortAccess;
import arcbasis._cocos.ArcBasisASTPortAccessCoCo;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CConnection;
import de.monticore.lang.sd4componenttesting._ast.ASTTestDiagram;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class PortAccessValid implements ArcBasisASTPortAccessCoCo {
  @Override
  public void check(ASTPortAccess node) {
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    Optional<PortSymbol> portSymbol;
    if (!node.isPresentComponent()) {
      portSymbol = mainComponent.getPort(node.getPort());
    } else {
      Optional<ComponentInstanceSymbol> componentInstance = mainComponent.getSubComponent(node.getComponent());
      if (!componentInstance.isPresent()) {
        Log.error(String.format(
          SD4ComponentTestingError.UNKNOWN_COMPONENT_INSTANCE_IN_PORT_ACCESS.toString(),
          node.getComponent(),
          node.getQName(),
          mainComponent.getName())
        );
        return;
      }
      ComponentTypeSymbol componentTypeSymbol = componentInstance.get().getType();

      portSymbol = componentTypeSymbol.getPort(node.getPort());
    }
    if (!portSymbol.isPresent()) {
      Log.error(String.format(SD4ComponentTestingError.UNKNOWN_PORT_ACCESS.toString(), node.getQName()));
    }
  }
}
