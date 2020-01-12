// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTResourcePort;
import montithings._cocos.MontiThingsASTResourcePortCoCo;

/**
 * TODO
 *
 * @author (last commit)
 */
public class LowerCaseResourcePort implements MontiThingsASTResourcePortCoCo {
  @Override
  public void check(ASTResourcePort node) {
    if (Character.isUpperCase(node.getName().charAt(0))){
      Log.error("0xMT133 Names of resource ports must start with a lowercase letter.",
              node.get_SourcePositionStart());
    }
  }
}
