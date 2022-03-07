// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings.util.MontiThingsError;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static montithings.util.PortUtil.getSetsPortsDeclaredByBehaviors;

public class BehaviorsDoNotUseTheSamePorts implements MontiThingsASTMTComponentTypeCoCo {

  @Override public void check(ASTMTComponentType node) {
    // Get all combinations of ports
    List<Set<String>> portNameSets = getSetsPortsDeclaredByBehaviors(node);

    // Report each set of ports only once
    Set<Set<String>> alreadyReported = new HashSet<>();

    // check there are no duplicates
    for (Set<String> portSet : portNameSets) {
      if (Collections.frequency(portNameSets, portSet) > 1 && !alreadyReported.contains(portSet)) {
        Log.error(String.format(MontiThingsError.MULTIPLE_BEHAVIORS_SAME_PORTS.toString(),
          portSet.toString(), node.getSymbol().getName()));
        alreadyReported.add(portSet);
      }
    }
  }

}
