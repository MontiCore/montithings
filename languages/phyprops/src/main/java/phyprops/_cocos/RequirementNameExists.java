// (c) https://github.com/MontiCore/monticore
package phyprops._cocos;

import de.se_rwth.commons.logging.Log;
import phyprops._ast.ASTRequirementStatement;
import phyprops.util.PhypropsError;

/**
 * Checks if import names refer to ComponentTypeSymbols.
 *
 * @author Julian Krebber
 */
public class RequirementNameExists implements PhypropsASTRequirementStatementCoCo {
  @Override
  public void check(ASTRequirementStatement node) {
    if(!node.isPresentComponentDefinition()){
      Log.error(
          String.format(PhypropsError.MISSING_REQUIREMENT_NAME.toString(),
              node.getName(),node.get_SourcePositionEnd().getLine(),node.get_SourcePositionEnd().getColumn()-node.getName().length()-1));
    }
  }
}
