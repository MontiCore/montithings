/* (c) https://github.com/MontiCore/monticore */
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.PortSymbol;
import montithings._ast.ASTAssumption;
import montithings._cocos.MontiThingsASTAssumptionCoCo;

import java.util.List;

/**
 * Assumptions may not use output ports in their expressions
 *
 * @author (last commit) kirchhof
 * @version , 13.02.2020
 * @since
 */
public class AssumptionDoesNotDependOnOutputPort implements MontiThingsASTAssumptionCoCo {

  @Override public void check(ASTAssumption node) {
    List <PortSymbol> ports = node.getPortsInGuardExpression();
    for (PortSymbol p : ports) {
      if (p.isOutgoing()) {
        Log.error("0xMT109 Assumption " + node.toString()
            + " may not depend on output port \"" + p.getName() + "\"");
      }
    }
  }
}
