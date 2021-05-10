// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTHookpoint;
import mtconfig.util.MTConfigError;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Checks that hookpoints use correct names
 */
public class HookpointExists implements MTConfigASTHookpointCoCo {
  final Set<String> validHookpoints = new HashSet<>(
    Arrays.asList("init", "include", "provide", "consume", "mqtt")
  );

  @Override public void check(ASTHookpoint node) {
    if (!validHookpoints.contains(node.getName())) {
      Log.error(String.format(MTConfigError.HOOKPOINT_EXISTS.toString(),
        node.getName(),
        String.join(", ", validHookpoints)));
    }
  }
}