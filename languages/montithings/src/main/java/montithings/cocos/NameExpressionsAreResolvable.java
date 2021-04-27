// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
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
        || ((IMontiThingsScope) node.getEnclosingScope()).resolvePort(referencedName).isPresent()
        || ((IMontiThingsScope) node.getEnclosingScope()).resolveFunction(referencedName)
        .isPresent();

    boolean isSetVariableDeclaration = checkSetdeclarations(node);

    if (!nameExists && !isSetVariableDeclaration) {
      Log.error(String.format(MontiThingsError.IDENTIFIER_UNKNOWN.toString(), node.getName()),
        node.get_SourcePositionStart());
    }
  }

  // ASTNameExpressions on the left side of an ASTAssignmentExpression are allowed to use
  // unknown variable names - they declare them
  protected boolean checkSetdeclarations(ASTNameExpression node) {
    if (node.getEnclosingScope().isPresentAstNode() &&
      node.getEnclosingScope().getAstNode() instanceof ASTSetComprehension) {
      ASTSetComprehension setComprehension = ((ASTSetComprehension) node.getEnclosingScope()
        .getAstNode());
      for (ASTSetComprehensionItem item : setComprehension.getSetComprehensionItemList()) {
        if (item.getExpression() instanceof ASTAssignmentExpression) {
          ASTAssignmentExpression assignment = (ASTAssignmentExpression) item.getExpression();
          if (assignment.getLeft() == node) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
