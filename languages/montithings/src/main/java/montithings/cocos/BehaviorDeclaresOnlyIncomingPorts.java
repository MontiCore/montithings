// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTInitBehavior;
import montithings._ast.ASTMTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings.util.MontiThingsError;

import java.util.stream.Collectors;

import static montithings.util.PortUtil.*;

public class BehaviorDeclaresOnlyIncomingPorts implements MontiThingsASTMTComponentTypeCoCo {

  @Override public void check(ASTMTComponentType node) {
    // Check init behaviors that apply to specific ports
    for (ASTInitBehavior initBehavior : getPortSpecificInitBehaviors(node)) {
      checkBehavior(initBehavior, node);
    }

    // Check behaviors that apply to specific ports
    for (ASTBehavior behavior : getAstBehaviors(node)) {
      checkBehavior(behavior, node);
    }
  }

  protected void checkBehavior(ASTMTBehavior behavior, ASTMTComponentType node) {
    for (PortSymbol p : getPresentPortsOfBehavior(behavior).stream()
      .filter(p -> !p.isIncoming())
      .collect(Collectors.toSet())) {
      Log.error(String.format(MontiThingsError.BEHAVIOR_REFERENCES_INVALID_PORT.toString(),
        behavior.getNameList().toString(), node.getSymbol().getName(), p.getName()));
    }
  }

}
