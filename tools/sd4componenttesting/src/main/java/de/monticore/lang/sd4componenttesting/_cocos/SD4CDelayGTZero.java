// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting._ast.ASTSD4CDelay;
import de.monticore.lang.sd4componenttesting.util.ComponentHelper;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

public class SD4CDelayGTZero implements SD4ComponentTestingASTSD4CDelayCoCo {
  @Override
  public void check(ASTSD4CDelay node) {
    if (!(ComponentHelper.getTimeInMillis(node.getSIUnitLiteral()) > 0)) {
      Log.error(String.format(SD4ComponentTestingError.DELAY_GREATER_THAN_ZERO.toString(), node.getSIUnitLiteral().getNumericLiteral()));
    }
  }
}
