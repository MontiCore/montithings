// (c) https://github.com/MontiCore/monticore
package portextensions.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;

/**
 * Checks that sync groups are no subset of another sync group
 * If this was allowed the larger sync group would be superfluous.
 *
 * @author (last commit)
 */
public class SyncGroupIsNoSubset implements ArcBasisASTComponentTypeCoCo {

  @Override public void check(ASTComponentType node) {
    //TODO: Write me
  }

  /*
  @Override
  public void check(ASTComponent astComponent) {

    List<Set<String>> syncSets = new ArrayList<>();
    List<ASTSyncStatement> syncGroups = getSyncGroups(astComponent);
    for (ASTSyncStatement syncGroup : syncGroups) {
      Set<String> set = syncGroupToSet(syncGroup);
      for (Set<String> syncSet : syncSets) {
        if (syncSet.containsAll(set) | set.containsAll(syncSet)) {
          Log.error("0xMT116 Sync Group " + syncGroup.getName()
                  + " should not be a subset of another sync group.",
              syncGroup.get_SourcePositionStart());
        }
      }
      syncSets.add(set);
    }

  }

   */

  /**
   * TODO modify since ASTControlBlock was removed.
   * @param comp
   * @return
   */
  /*
  private List<ASTSyncStatement> getSyncGroups(ASTComponent comp) {
    return new ArrayList<ASTSyncStatement>();/*comp.getBody().getElementList()
        .stream()
        .filter(ASTControlBlock.class::isInstance)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(ASTSyncStatement.class::isInstance)
        .map(ASTSyncStatement.class::cast)
        .collect(Collectors.toList());
  }


  private Set<String> syncGroupToSet(ASTSyncStatement sync) {
    return new HashSet<>(sync.getSyncedPortList());
  }

   */
}
