// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings.util.MontiThingsError;

/**
 * Checks that all name expressions in a behavior block actually refer to something
 * useful and thus prevent, e.g.,
 *
 * @since 06.01.21
 */
public class NameExpressionsAreResolvable implements ExpressionsBasisASTNameExpressionCoCo {
  @Override public void check(ASTNameExpression node) {
    String referencedName = node.getName();
    boolean nameExists =
      ((IMontiThingsScope) node.getEnclosingScope()).resolveVariable(referencedName).isPresent()
        || ((IMontiThingsScope) node.getEnclosingScope()).resolveField(referencedName).isPresent()
        || ((IMontiThingsScope) node.getEnclosingScope()).resolvePort(referencedName).isPresent();

    if (!nameExists) {
      Log.error(String.format(MontiThingsError.IDENTIFIER_UNKNOWN.toString(), node.getName()),
        node.get_SourcePositionStart());
    }
  }
}
