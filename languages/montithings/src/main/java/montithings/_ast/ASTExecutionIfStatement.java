/* (c) https://github.com/MontiCore/monticore */
package montithings._ast;

import arcbasis._symboltable.PortSymbol;
import montithings.helper.ExpressionUtil;

import java.util.List;
import java.util.Optional;

/**
 * TODO
 *
 * @version , 13.02.2020
 */
public class ASTExecutionIfStatement extends ASTExecutionIfStatementTOP {
  public List<PortSymbol> getPortsInGuardExpression() {
    return ExpressionUtil.getPortsInGuardExpression(getGuard());
  }
}
