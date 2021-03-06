// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import montithings.tools.sd4componenttesting._ast.ASTSD4CConnection;
import montithings.tools.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;
import montithings.tools.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;

public class SD4CConnectionValid implements SD4ComponentTestingASTSD4CConnectionCoCo {
  @Override
  public void check(ASTSD4CConnection node) {

    SD4ComponentTestingFullPrettyPrinter sd4ComponentTestingFullPrettyPrinter = new SD4ComponentTestingFullPrettyPrinter();
    String nodeString = sd4ComponentTestingFullPrettyPrinter.prettyprint(node);
    //Case 1:  ->  : VALUE;
    if (!node.isPresentSource() && node.getTargetList().isEmpty()) {
      Log.error(String.format(SD4ComponentTestingError.CONNECTION_NOT_VALID.toString(), nodeString));
    }

    if (node.getTargetList().size() != node.getValueList().size()) {
      if (node.getValueList().size() != 1) {
        Log.error(String.format(SD4ComponentTestingError.CONNECTION_NOT_VALID_WRONG_VALUE_AMOUNT.toString(), nodeString));
      }
    }
  }
}
