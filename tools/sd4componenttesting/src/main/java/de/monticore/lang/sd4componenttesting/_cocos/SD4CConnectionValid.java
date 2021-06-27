// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import arcbasis._ast.ASTPortAccess;
import de.monticore.lang.sd4componenttesting._ast.ASTTestDiagram;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CConnection;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

public class SD4CConnectionValid implements SD4ComponentTestingASTTestDiagramCoCo {
  @Override
  public void check(ASTTestDiagram node) {
    Optional<ComponentTypeSymbol> comp = node.getEnclosingScope().resolveComponentType(node.getMainComponent());
    if (!comp.isPresent()) {
      Log.error(
          String.format(SD4ComponentTestingError.NO_MAIN_COMPONENT_IMPLEMENTATION.toString(), node.getMainComponent()));
      return;
    }
    ComponentTypeSymbol component = comp.get();
    node.getSD4CElementList().forEach(elem -> {
      if (!elem.getClass().equals(ASTSD4CConnection.class))
        return;

      ASTSD4CConnection connection = (ASTSD4CConnection) elem;
      connection.getTargetList().forEach(target -> {
        check(component, target);
      });
    });
  }

  public void check(ComponentTypeSymbol component, ASTPortAccess portAccess) {
    Optional<PortSymbol> portSymbol;
    if (!portAccess.isPresentComponent()) {
      portSymbol = component.getPort(portAccess.getPort(), true);
      // We accept an output port as target if it is a port of the component itself.
      return;
    } else {
      portSymbol = component.getSubComponent(portAccess.getComponent())
          .flatMap(componentInstanceSymbol -> componentInstanceSymbol.getType().getPort(portAccess.getPort(), true));
    }
    if (!portSymbol.isPresent()) {
      Log.error(String.format((SD4ComponentTestingError.UNKNOWN_PORT_ACCESS).toString(), portAccess.getQName()),
          portAccess.get_SourcePositionStart());
      return;
    }
    PortSymbol port = portSymbol.get();
    if(!port.isIncoming()) {
      Log.error(String.format((SD4ComponentTestingError.TARGET_IS_OUTPUT_PORT).toString(), portAccess.getQName()),
          portAccess.get_SourcePositionStart());
      return;
    }
  }
}
