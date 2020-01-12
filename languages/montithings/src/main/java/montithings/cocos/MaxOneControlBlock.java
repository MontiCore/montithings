// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montithings._ast.ASTControlBlock;

/**
 * Checks that there is only a single control block in a component
 *
 * @author (last commit) JFuerste
 */
public class MaxOneControlBlock implements MontiArcASTComponentCoCo {

  @Override
  public void check(ASTComponent node) {
    long controlBlockCount = node.getBody()
            .getElementList()
            .stream()
            .filter(e -> e instanceof ASTControlBlock)
            .count();

    if (controlBlockCount > 1){
      Log.error("0xMT101 There are multiple control blocks in component " + node.getName() ,
              node.get_SourcePositionStart());
    }
  }
}
