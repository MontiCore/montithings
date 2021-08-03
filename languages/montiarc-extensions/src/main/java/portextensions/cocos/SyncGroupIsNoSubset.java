// (c) https://github.com/MontiCore/monticore
package portextensions.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that sync groups are no subset of another sync group
 * If this was allowed the larger sync group would be superfluous.
 */
public class SyncGroupIsNoSubset implements ArcBasisASTComponentTypeCoCo {

  @Override public void check(ASTComponentType node) {
    Set<List<String>> syncGroups = node.getBody().getArcElementList().stream()
      .filter(e -> e instanceof portextensions._ast.ASTSyncStatement)
      .map(e -> ((portextensions._ast.ASTSyncStatement) e).getSyncedPortList())
      .collect(Collectors.toSet());

    for (List<String> currentGroup : syncGroups) {
      for (List<String> otherGroup : syncGroups) {
        if (otherGroup != currentGroup && otherGroup.containsAll(currentGroup)) {
          Log.error("0xMT116 Sync Group {" + currentGroup
            + "} should not be a subset of another sync group.");
        }
      }
    }
  }
}
