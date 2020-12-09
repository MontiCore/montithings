// (c) https://github.com/MontiCore/monticore
package mtconfig.util;

import mtconfig._ast.ASTCompConfig;
import mtconfig._ast.ASTElement;
import mtconfig._ast.ASTMTConfigUnit;

import java.util.HashSet;
import java.util.Set;

public class ASTMTConfigUnitUtil {
  /**
   * Finds all component names referenced by a config unit
   * @param node the config unit whose component references to find
   * @return the set of all component names referenced by the config unit
   */
  static public Set<String> findComponentNamesInConfigUnit(ASTMTConfigUnit node) {
    Set<String> foundNames = new HashSet<>();

    for (ASTElement elem : node.getElementList()) {
      if (elem instanceof ASTCompConfig) {
        ASTCompConfig config = (ASTCompConfig)elem;
        foundNames.add(config.getName());
      }
    }

    return foundNames;
  }
}
