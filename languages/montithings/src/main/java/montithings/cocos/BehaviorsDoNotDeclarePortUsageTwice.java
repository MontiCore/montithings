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

import java.util.Collections;
import java.util.Optional;

import static montithings.util.PortUtil.getAstBehaviors;
import static montithings.util.PortUtil.getPortSpecificInitBehaviors;

public class BehaviorsDoNotDeclarePortUsageTwice implements MontiThingsASTMTComponentTypeCoCo {

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
    for (Optional<PortSymbol> port : behavior.getNamesSymbolList()) {
      if (Collections.frequency(behavior.getNamesSymbolList(), port) > 1) {
        Log.error(String.format(MontiThingsError.BEHAVIOR_REFERENCES_PORT_MULTIPLE_TIMES.toString(),
          behavior.getNameList().toString(), node.getSymbol().getName(), port.get().getName()));
      }
    }
  }

}
