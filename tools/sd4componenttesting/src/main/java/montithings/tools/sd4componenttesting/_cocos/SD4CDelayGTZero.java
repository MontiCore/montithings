// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import montithings.tools.sd4componenttesting._ast.ASTSD4CDelay;
import montithings.tools.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;
import montithings.tools.sd4componenttesting.util.ComponentHelper;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;
import montithings.tools.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;

public class SD4CDelayGTZero implements SD4ComponentTestingASTSD4CDelayCoCo {
  @Override
  public void check(ASTSD4CDelay node) {
    if (!(ComponentHelper.getTimeInMillis(node.getSIUnitLiteral()) > 0)) {
      SD4ComponentTestingFullPrettyPrinter pp = new SD4ComponentTestingFullPrettyPrinter();
      Log.error(String.format(SD4ComponentTestingError.DELAY_GREATER_THAN_ZERO.toString(), pp.prettyprint(node)));
    }
  }
}
