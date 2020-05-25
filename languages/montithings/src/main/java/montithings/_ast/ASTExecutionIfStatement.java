/* (c) https://github.com/MontiCore/monticore */
package montithings._ast;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import montiarc._ast.ASTGuardExpression;
import montiarc._symboltable.PortSymbol;
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
    return ExpressionUtil.getPortsInGuardExpression(getGuard().getExpression());
  }

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public ASTExecutionIfStatement() {
  }

  public ASTExecutionIfStatement(ASTGuardExpression guard,
      Optional<String> method, List<ASTPortValuation> portValuations,
      Optional<ASTIntLiteral> priority) {
    super(guard, method, portValuations, priority);
  }
}
