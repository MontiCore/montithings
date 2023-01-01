// (c) https://github.com/MontiCore/monticore
package network.cocos;

import de.se_rwth.commons.logging.Log;
import network._cocos.NetworkASTNetCoCo;
import network._ast.ASTNet; 
import org.assertj.core.util.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Checks that all lines, i.e. strings, within a tab have the same length.
 *
 * @author (last commit) kirchhof
 * @version 1.0, 20.03.19
 * @since 1.0
 */
public class MinOneConnection implements NetworkASTNetCoCo {

  @VisibleForTesting
  public static final String THERE_ARE_TOO_FEW_CONNECTIONS =
      "0xY0100 there are not enough connections (min 1)";

  @Override public void check(ASTNet node) {
    if(node.getConnectionList().size() == 0){
      Log.error(THERE_ARE_TOO_FEW_CONNECTIONS);
    }
  }
}
