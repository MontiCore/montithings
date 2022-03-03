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

import java.util.Set;

import static montithings.util.PortUtil.*;

public class BehaviorDeclaresAllPortsItUses implements MontiThingsASTMTComponentTypeCoCo {

  @Override public void check(ASTMTComponentType node) {
    if (node.isPresentSymbol()) {
      // Check init behaviors that apply to specific ports
      for (ASTInitBehavior initBehavior : getPortSpecificInitBehaviors(node)) {
        checkAllPortsUsedInBehaviorAreDeclaredInBehaviorDeclaration(initBehavior, node);
      }

      // Check behaviors that apply to specific ports
      for (ASTBehavior behavior : getAstBehaviors(node)) {
        checkAllPortsUsedInBehaviorAreDeclaredInBehaviorDeclaration(behavior, node);
      }
    }
  }

  protected void checkAllPortsUsedInBehaviorAreDeclaredInBehaviorDeclaration(ASTMTBehavior behavior,
    ASTMTComponentType node) {

    Set<PortSymbol> portsInBehavior = getReferencedPorts(behavior.getMCJavaBlock());

    for (PortSymbol portSymbolInBehavior : portsInBehavior) {
      if (getPresentPortsOfBehavior(behavior).stream()
        .noneMatch(p -> p.equals(portSymbolInBehavior))
      ) {
        Log.error(String.format(MontiThingsError.BEHAVIOR_USES_UNDECLARED_PORT.toString(),
          behavior.getNameList().toString(), node.getSymbol().getName(),
          portSymbolInBehavior.getName()));
      }
    }
  }

}
