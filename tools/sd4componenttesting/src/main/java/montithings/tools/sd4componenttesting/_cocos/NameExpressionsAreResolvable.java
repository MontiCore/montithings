// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import montithings.tools.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;
import montithings.tools.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;

public class NameExpressionsAreResolvable implements ExpressionsBasisASTNameExpressionCoCo {
  @Override public void check(ASTNameExpression node) {
    String referencedName = node.getName();
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    boolean nameExists = mainComponent.getSubComponent(referencedName).isPresent()
      || mainComponent.getPort(referencedName).isPresent();

    if (!nameExists) {
      Log.error(String.format(SD4ComponentTestingError.IDENTIFIER_UNKNOWN.toString(), node.getName()),
        node.get_SourcePositionStart());
    }
  }
}
