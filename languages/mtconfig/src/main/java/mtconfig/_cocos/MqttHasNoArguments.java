// (c) https://github.com/MontiCore/monticore
package mtconfig._cocos;

import de.se_rwth.commons.logging.Log;
import mtconfig._ast.ASTHookpoint;
import mtconfig.util.MTConfigError;

public class MqttHasNoArguments implements MTConfigASTHookpointCoCo {

  @Override public void check(ASTHookpoint node) {
    if (node.getName().equalsIgnoreCase("mqtt")
      && node.isPresentArguments()
      && node.getArguments().sizeExpressions() != 0) {
      Log.error(String.format(MTConfigError.MQTT_NO_ARGS.toString(),
        node.getName()));
    }
  }
}
