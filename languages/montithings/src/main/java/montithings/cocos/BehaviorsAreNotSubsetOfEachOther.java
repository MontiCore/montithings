// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings.util.MontiThingsError;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static montithings.util.PortUtil.getSetsPortsDeclaredByBehaviors;

public class BehaviorsAreNotSubsetOfEachOther implements MontiThingsASTMTComponentTypeCoCo {

  @Override public void check(ASTMTComponentType node) {
    List<Set<String>> portNameSets = getSetsPortsDeclaredByBehaviors(node);

    for (Set<String> ports : portNameSets) {
      for (Set<String> portsForWhichSubsetExists : portNameSets.stream()
        .filter(s -> !s.equals(ports))
        .filter(s -> s.containsAll(ports) && portNameSets.indexOf(s) > portNameSets.indexOf(ports))
        .collect(Collectors.toSet())
      ) {
        Log.warn(String.format(MontiThingsError.BEHAVIOR_PORTS_USED_ALREADY.toString(),
          node.getSymbol().getName(), portsForWhichSubsetExists));
      }
    }
  }

}
