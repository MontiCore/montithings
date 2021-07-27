// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.lang.sd4componenttesting._ast.ASTSD4CDelay;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;
import de.se_rwth.commons.logging.Log;

public class SD4CDelayValidUnit implements SD4ComponentTestingASTSD4CDelayCoCo {
  @Override
  public void check(ASTSD4CDelay node) {
    String unit = SIUnitsPrettyPrinter.prettyprint(node.getSIUnitLiteral().getSIUnit());
    switch (unit) {
      case ("ns"):
      case ("μs"):
      case ("ms"):
      case ("s"):
      case ("min"):
      case ("h"):
        return;
      default:
        Log.error(String.format(SD4ComponentTestingError.DELAY_UNIT_UNKNOWN.toString(), unit));
    }
  }
}
