// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.PortSymbolTOP;
import de.se_rwth.commons.logging.Log;
import montithings._visitor.FindOutgoingPorts;
import montithings._visitor.MontiThingsPrettyPrinter;
import montithings._visitor.MontiThingsPrettyPrinterDelegator;
import montithings.util.MontiThingsError;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._cocos.PrePostConditionASTPostconditionCoCo;

import java.util.stream.Collectors;

public class PostcondUsesOnlyOneOutport implements PrePostConditionASTPostconditionCoCo {


  @Override public void check(ASTPostcondition node) {
    FindOutgoingPorts visitor = new FindOutgoingPorts();
    node.getGuard().accept(visitor);
    if (visitor.getReferencedPorts().size() > 1) {
      MontiThingsPrettyPrinterDelegator pp = new MontiThingsPrettyPrinterDelegator();
      String expression = pp.prettyprint(node.getGuard());
      String ports = visitor.getReferencedPorts().stream()
        .map(port -> "'" + port.getName() + "'")
        .collect(Collectors.joining(", "));

      Log.error(String.format(MontiThingsError.POSTCONDITION_MULTIPLE_OUTPORTS.toString(), expression, ports),
        node.get_SourcePositionStart());
    }
  }
}
