// (c) https://github.com/MontiCore/monticore
package portextensions.cocos;

import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import de.se_rwth.commons.logging.Log;
import portextensions._ast.ASTAnnotatedPort;
import portextensions._ast.ASTBufferedPort;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that ports used in batch statements exist and are incoming
 *
 * @author (last commit) JFuerste
 */
public class PortsInBatchStatementAreIncoming implements ArcBasisASTComponentTypeCoCo {

  @Override public void check(ASTComponentType node) {
    Set<ASTPortDeclaration> bufferedPorts = node.getBody().getArcElementList().stream()
      .filter(e -> e instanceof ASTAnnotatedPort)
      .map(e -> (ASTAnnotatedPort) e)
      .filter(e -> e.getPortAnnotation() instanceof ASTBufferedPort)
      .map(ASTComponentInterface::getPortDeclarationList)
      .flatMap(List::stream)
      .collect(Collectors.toSet());

    for (ASTPortDeclaration p : bufferedPorts) {
      if (!p.isIncoming()) {
        Log.error("0xMAE0010 Port '" + p.getPortList() + "' is buffered but not incoming.");
      }
    }
  }
}
