// (c) https://github.com/MontiCore/monticore
package portextensions.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import de.se_rwth.commons.logging.Log;
import portextensions._ast.ASTSyncStatement;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that ports in sync statements exist and are incoming
 */
public class PortsInSyncGroupAreIncoming implements ArcBasisASTComponentTypeCoCo {

  @Override public void check(ASTComponentType node) {
    Set<String> syncedPorts = node.getBody().getArcElementList().stream()
      .filter(e -> e instanceof ASTSyncStatement)
      .map(e -> ((ASTSyncStatement) e).getSyncedPortList())
      .flatMap(List::stream)
      .collect(Collectors.toSet());

    for (String portName : syncedPorts) {
      if (!node.getSpannedScope().resolvePort(portName).isPresent() ||
        !node.getSpannedScope().resolvePort(portName).get().isIncoming()) {

        Log.error("0xMAE0020 Port '" + portName
          + "' is in sync statement but does not refer to an incoming port");
      }
    }
  }
}
