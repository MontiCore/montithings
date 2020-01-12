// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTCalculationInterval;
import montithings._cocos.MontiThingsASTCalculationIntervalCoCo;

/**
 * Checks that the update interval is a positive integer
 *
 * @author (last commit) JFuerste
 */
public class UpdateIntervalPositive implements MontiThingsASTCalculationIntervalCoCo {
  @Override
  public void check(ASTCalculationInterval node) {
    if (!(node.getInterval().getValue() > 0)){
      Log.error("0xMT118 The update interval for the component should be a positive integer value",
              node.get_SourcePositionStart());
    }
  }
}
