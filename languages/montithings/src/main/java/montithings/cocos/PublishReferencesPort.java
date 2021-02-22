// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTPublishPort;
import montithings._cocos.MontiThingsASTPublishPortCoCo;
import montithings.util.MontiThingsError;

public class PublishReferencesPort implements MontiThingsASTPublishPortCoCo {
  @Override public void check(ASTPublishPort node) {
    Preconditions.checkArgument(node != null);
    for (int i = 0; i < node.getPublishedPortsList().size(); i++) {
      String portName = node.getPublishedPorts(i);
      boolean portExists = node.getEnclosingScope().resolvePort(portName).isPresent();
      if (!portExists) {
        Log.error(String.format(MontiThingsError.PUBLISH_IDENTIFIER_UNKNOWN.toString(),
          portName, node.get_SourcePositionStart()));
      }
    }
  }
}
