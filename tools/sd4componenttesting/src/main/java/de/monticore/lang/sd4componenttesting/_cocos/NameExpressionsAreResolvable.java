// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTNameExpressionCoCo;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehension;
import de.monticore.ocl.setexpressions._ast.ASTSetComprehensionItem;
import de.se_rwth.commons.logging.Log;

public class NameExpressionsAreResolvable implements ExpressionsBasisASTNameExpressionCoCo {
  @Override public void check(ASTNameExpression node) {
    String referencedName = node.getName();
    ComponentTypeSymbol mainComponent = ((ISD4ComponentTestingArtifactScope) node.getEnclosingScope()).getMainComponentTypeSymbol();

    boolean nameExists = mainComponent.getSubComponent(referencedName).isPresent()
      || mainComponent.getPort(referencedName).isPresent();

    boolean isSetVariableDeclaration = checkSetdeclarations(node);

    if (!nameExists && !isSetVariableDeclaration) {
      Log.error(String.format(SD4ComponentTestingError.IDENTIFIER_UNKNOWN.toString(), node.getName()),
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
