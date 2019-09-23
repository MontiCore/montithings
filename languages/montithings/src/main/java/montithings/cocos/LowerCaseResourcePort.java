/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
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
