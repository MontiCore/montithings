// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTSyncStatement;
import montithings._cocos.MontiThingsASTSyncStatementCoCo;

/**
 * Sync group names should start with an uppercase to differentiate themselves from
 * ports in Execution statements
 *
 * @author (last commit) JFuerste
 */
public class SyncGroupNamesUppercase implements MontiThingsASTSyncStatementCoCo {
  @Override
  public void check(ASTSyncStatement node) {
    if (!Character.isUpperCase(node.getName().charAt(0))){
      Log.error("0xMT116 Names of synchronization groups must start with a capital letter.",
              node.get_SourcePositionStart());
    }
  }
}
