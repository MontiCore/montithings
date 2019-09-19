/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTSyncStatement;
import montithings._cocos.MontiThingsASTSyncStatementCoCo;

/**
 * Checks that sync groups are not empty
 *
 * @author (last commit) JFuerste
 */
public class SyncGroupHasAtLeastOneElement implements MontiThingsASTSyncStatementCoCo {
  @Override
  public void check(ASTSyncStatement node) {
    if (node.getSyncedPortList().isEmpty()){
      Log.error("0xMT115 Synchronization groups must contain at least one port.",
              node.get_SourcePositionStart());
    }
  }
}
