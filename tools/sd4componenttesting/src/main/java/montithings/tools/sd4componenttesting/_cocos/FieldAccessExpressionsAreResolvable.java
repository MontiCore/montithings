// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTFieldAccessExpressionCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montithings.tools.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;
import montithings.tools.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;

public class FieldAccessExpressionsAreResolvable implements CommonExpressionsASTFieldAccessExpressionCoCo {
  @Override public void check(ASTFieldAccessExpression node) {
    if (!(node.getExpression() instanceof ASTNameExpression)) {
      Log.error(SD4ComponentTestingError.EXPRESSION_FIELD_ACCESS_EXPRESSION_NO_COMPONENT_NAME.toString());
      return;
    }

    String portName = node.getName();
    String componentName = ((ASTNameExpression)node.getExpression()).getName();
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    ComponentInstanceSymbol subComponent = mainComponent.getSubComponent(componentName).get();

    if (!subComponent.getType().getPort(portName).isPresent()) {
      Log.error(String.format(SD4ComponentTestingError.EXPRESSION_FIELD_ACCESS_EXPRESSION_NO_COMPONENT_FOUND.toString(), portName));
    }
  }
}
