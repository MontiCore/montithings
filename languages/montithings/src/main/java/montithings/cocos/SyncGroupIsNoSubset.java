/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montithings._ast.ASTControlBlock;
import montithings._ast.ASTSyncStatement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that sync groups are no subset of another sync group
 * If this was allowed the larger sync group would be superfluous.
 *
 * @author (last commit)
 */
public class SyncGroupIsNoSubset implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent astComponent) {

    List<Set<String>> syncSets = new ArrayList<>();
    List<ASTSyncStatement> syncGroups = getSyncGroups(astComponent);
    for (ASTSyncStatement syncGroup : syncGroups) {
      Set<String> set = syncGroupToSet(syncGroup);
      for (Set<String> syncSet : syncSets) {
        if (syncSet.containsAll(set) | set.containsAll(syncSet)){
          Log.error("0xMT116 Sync Group " + syncGroup.getName() + " should not be a subset of another sync group.",
                  syncGroup.get_SourcePositionStart());
        }
      }
      syncSets.add(set);
    }

  }

  private List<ASTSyncStatement> getSyncGroups(ASTComponent comp){
    return comp.getBody().getElementList()
            .stream()
            .filter(ASTControlBlock.class::isInstance)
            .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
            .filter(ASTSyncStatement.class::isInstance)
            .map(ASTSyncStatement.class::cast)
            .collect(Collectors.toList());
  }

  private Set<String> syncGroupToSet(ASTSyncStatement sync){
    return new HashSet<>(sync.getSyncedPortList());
  }
}
