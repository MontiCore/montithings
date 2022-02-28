// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings._visitor.NameExpression2TypeValidator;
import montithings.util.MontiThingsError;

/**
 * Checks that all name expressions in a behavior block actually refer to something
 * useful and thus prevent
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

    if (!nameExists && !isSetVariableDeclaration && !checkForTypeAccess(node)) {
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

  /**
   * Checks whether the given name expression is actually part of a qualified name that refers to a type.
   *
   * @param node Input name expression
   * @return true, if name expression is part of type reference, false otherwise
   */
  protected boolean checkForTypeAccess(ASTNameExpression node) {
    if (node.getEnclosingScope() != null && node.getEnclosingScope().isPresentAstNode()) {
      NameExpression2TypeValidator typeValidator = new NameExpression2TypeValidator();
      return typeValidator.isTypeReference(node);
    }
    return false;
  }
}
