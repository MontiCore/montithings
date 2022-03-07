// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.PortSymbol;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings.util.MontiThingsError;
import montithings.util.PortUtil;

import java.util.ArrayList;
import java.util.List;

import static montithings.util.PortUtil.*;

public class BehaviorsLeaveNoIncomingPortsUnused implements MontiThingsASTMTComponentTypeCoCo {

  @Override public void check(ASTMTComponentType node) {
    List<PortSymbol> unusedIncomingPorts = node.getSymbol().getAllIncomingPorts();
    getAstBehaviors(node).stream()
      .map(PortUtil::getPresentPortsOfBehavior)
      .forEach(unusedIncomingPorts::removeAll);

    if (!nonPortSpecificBehaviorExists(node.getSymbol())
      && portSpecificBehaviorExists(node.getSymbol())
      && !unusedIncomingPorts.isEmpty()) {
      List<String> unusedIncomingPortsNames = new ArrayList<>();
      for (PortSymbol portSymbol : unusedIncomingPorts) {
        unusedIncomingPortsNames.add(portSymbol.getName());
      }
      Log.warn(String.format(MontiThingsError.INCOMING_PORTS_NOT_USED.toString(),
        unusedIncomingPortsNames, node.getSymbol().getName()));
    }
  }

}
