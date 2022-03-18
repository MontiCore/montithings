// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting._cocos;

import de.monticore.expressions.assignmentexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.lang.sd4componenttesting._ast.ASTSD4CExpression;
import de.monticore.lang.sd4componenttesting.util.SD4ComponentTestingError;
import de.se_rwth.commons.logging.Log;

public class CurrentlyNotSupported implements SD4ComponentTestingASTSD4CExpressionCoCo {
  public void check(ASTSD4CExpression node) {
    ASTExpression expression = node.getExpression();

    if (expression instanceof ASTIncSuffixExpression
      || expression instanceof ASTDecSuffixExpression
      || expression instanceof ASTIncPrefixExpression
      || expression instanceof ASTDecPrefixExpression
      || expression instanceof ASTAssignmentExpression
    ) {
      Log.error(SD4ComponentTestingError.ASSIGNMENT_EXPRESSIONS.toString());
    }
  }
}
