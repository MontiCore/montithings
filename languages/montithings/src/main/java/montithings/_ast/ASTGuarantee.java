/* (c) https://github.com/MontiCore/monticore */
package montithings._ast;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import arcbasis._symboltable.PortSymbol;
import montithings.helper.ExpressionUtil;

import java.util.List;

/**
 * TODO
 *
 * @author (last commit) kirchhof
 * @version , 13.02.2020
 * @since
 */
public class ASTGuarantee extends ASTGuaranteeTOP {
  public List<PortSymbol> getPortsInGuardExpression() {
    return ExpressionUtil.getPortsInGuardExpression(getGuard());
  }

  @Override public String toString() {
    return ExpressionUtil.printExpression(this.getGuard());
  }
}
