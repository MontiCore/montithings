// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig.util.MTConfigError;


import java.util.Set;

import static mtconfig.util.ASTMTConfigUnitUtil.findComponentNamesInConfigUnit;

/**
 * All configs in a file must refer to the same component
 */
public class AllConfigsReferToTheSameComponent implements MTConfigASTMTConfigUnitCoCo {

  /**
   * Checks that a config unit references exactly one component
   */
  @Override public void check(ASTMTConfigUnit node) {
    Set<String> componentNamesInConfig = findComponentNamesInConfigUnit(node);

    if (componentNamesInConfig.size() != 1) {
      Log.error(String.format(MTConfigError.MULTIPLE_COMPONENTS.toString(),
        String.join(", ", componentNamesInConfig)));
    }
  }
}
