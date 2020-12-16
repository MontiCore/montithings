// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTRequirementStatement;
import mtconfig.util.MTConfigError;

/**
 * Checks if import names refer to ComponentTypeSymbols.
 *
 * @author Julian Krebber
 */
public class RequirementNameExists implements MTConfigASTRequirementStatementCoCo {
  @Override
  public void check(ASTRequirementStatement node) {
    if (!node.isPresentComponentDefinition()) {
      Log.error(String.format(MTConfigError.MISSING_REQUIREMENT_NAME.toString(),
        node.getName(), node.get_SourcePositionEnd().getLine(),
        node.get_SourcePositionEnd().getColumn() - node.getName().length() - 1));
    }
  }
}
