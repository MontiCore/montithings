// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTCompConfig;
import mtconfig.util.MTConfigError;

/**
 * Checks if import names refer to ComponentTypeSymbols.
 *
 * @author Julian Krebber
 */
public class CompExists implements MTConfigASTCompConfigCoCo {
  @Override
  public void check(ASTCompConfig node) {
    if(!node.isPresentNameDefinition()){
      Log.error(
          String.format(MTConfigError.MISSING_REQUIREMENT_NAME.toString(),
              node.getName(),node.get_SourcePositionEnd().getLine(),node.get_SourcePositionEnd().getColumn()-node.getName().length()-1));
    }
  }
}
