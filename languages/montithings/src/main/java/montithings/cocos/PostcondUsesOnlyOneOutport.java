// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._visitor.FindOutgoingPorts;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import montithings.util.MontiThingsError;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._cocos.PrePostConditionASTPostconditionCoCo;

import java.util.stream.Collectors;

public class PostcondUsesOnlyOneOutport implements PrePostConditionASTPostconditionCoCo {

  @Override public void check(ASTPostcondition node) {
    FindOutgoingPorts visitor = new FindOutgoingPorts();
    node.getGuard().accept(visitor.createTraverser());
    if (visitor.getReferencedPorts().size() > 1) {
      MontiThingsFullPrettyPrinter pp = new MontiThingsFullPrettyPrinter();
      String expression = pp.prettyprint(node.getGuard());
      String ports = visitor.getReferencedPorts().stream()
        .map(port -> "'" + port.getName() + "'")
        .collect(Collectors.joining(", "));

      Log.error(String
          .format(MontiThingsError.POSTCONDITION_MULTIPLE_OUTPORTS.toString(), expression, ports),
        node.get_SourcePositionStart());
    }
  }
}
