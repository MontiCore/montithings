// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTControlBlock;
import montithings._ast.ASTControlStatement;
import montithings._cocos.MontiThingsASTControlBlockCoCo;
import montithings._cocos.MontiThingsASTControlStatementCoCo;

/**
 * Checks that control blocks are not empty
 *
 * @author (last commit) JFuerste
 */
public class ControlBlockNotEmpty implements MontiThingsASTControlBlockCoCo {

  @Override
  public void check(ASTControlBlock node) {
    if (node.getControlStatementList().isEmpty()){
      Log.error("0xMT102 Control Blocks should not be empty!", node.get_SourcePositionStart());
  }
}
}
