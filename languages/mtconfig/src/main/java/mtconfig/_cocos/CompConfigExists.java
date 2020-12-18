// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTCompConfig;
import mtconfig.util.MTConfigError;

/**
 * Checks if component names refer to ComponentTypeSymbols.
 *
 * @author Julian Krebber
 */
public class CompConfigExists implements MTConfigASTCompConfigCoCo {
  @Override
  public void check(ASTCompConfig node) {
    if (node.getComponentTypeSymbol() == null) {
      Log.error(String.format(MTConfigError.MISSING_COMPONENT_NAME.toString(),
        node.getName(), node.get_SourcePositionEnd().getLine(),
        node.get_SourcePositionEnd().getColumn() - node.getName().length() - 1));
    }
  }
}
