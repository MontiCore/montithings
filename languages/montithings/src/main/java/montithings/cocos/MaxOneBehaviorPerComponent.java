/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTBehaviorElement;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;

/**
 * Checks that there is only a single behavior element e.g ExecutionBlocks
 * in a component
 *
 * @author (last commit)
 */
public class MaxOneBehaviorPerComponent implements MontiArcASTComponentCoCo {


  @Override
  public void check(ASTComponent node) {
    long count = node.getBody().getElementList()
            .stream()
            .filter(e -> e instanceof ASTBehaviorElement)
            .count();
    if (count > 1){
      Log.error("0xMT110 There exists more than one behavior element in component " + node.getName(),
              node.get_SourcePositionStart());
    }

  }
}
