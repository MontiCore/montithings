// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import behavior._ast.ASTLogStatement;
import behavior._cocos.BehaviorASTLogStatementCoCo;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings.util.MontiThingsError;

import java.util.List;

/**
 * Checks that log function cannot reference variables that do not exist
 */
public class LoggedVariablesAreResolvable implements BehaviorASTLogStatementCoCo {
  @Override public void check(ASTLogStatement node) {
    List<String> referencedVariables = node.getReferencedVariables();
    for (String referencedName : referencedVariables) {
      boolean nameExists =
        node.getEnclosingScope().resolveVariable(referencedName).isPresent()
          || node.getEnclosingScope().resolveField(referencedName).isPresent()
          || node.getEnclosingScope().resolvePort(referencedName).isPresent();

      if (!nameExists) {
        Log.error(String.format(MontiThingsError.LOG_IDENTIFIER_UNKNOWN.toString(), referencedName),
          node.get_SourcePositionStart());
      }
    }
  }
}
