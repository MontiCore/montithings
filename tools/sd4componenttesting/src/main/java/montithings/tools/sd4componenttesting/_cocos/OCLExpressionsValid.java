// (c) https://github.com/MontiCore/monticore
package montithings.tools.sd4componenttesting._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montithings.tools.sd4componenttesting._ast.ASTSD4CExpression;
import montithings.tools.sd4componenttesting.util.SD4ComponentTestingError;
import de.monticore.ocl.oclexpressions._ast.*;
import de.monticore.ocl.setexpressions._ast.*;
import de.se_rwth.commons.logging.Log;
import setdefinitions._ast.ASTSetValueRegEx;

public class OCLExpressionsValid implements SD4ComponentTestingASTSD4CExpressionCoCo {

  @Override
  public void check(ASTSD4CExpression node) {
    ASTExpression expression = node.getExpression();

    if (expression instanceof ASTSetAndExpression) {
      Log.error(SD4ComponentTestingError.EXPRESSION_SET_AND_EXPRESSION.toString());
    }
    else if (expression instanceof ASTSetOrExpression) {
      Log.error(SD4ComponentTestingError.EXPRESSION_SET_OR_EXPRESSION.toString());
    }
    else if (expression instanceof ASTSetUnionExpression) {
      Log.error(SD4ComponentTestingError.EXPRESSION_SET_UNION_EXPRESSION.toString());
    }
    else if (expression instanceof ASTSetIntersectionExpression) {
      Log.error(SD4ComponentTestingError.EXPRESSION_SET_UNION_EXPRESSION.toString());
    }
    else if (expression instanceof ASTSetComprehension) {
      if (((ASTSetComprehension) expression).getLeft().isPresentExpression()) {
        Log.error(SD4ComponentTestingError.EXPRESSION_LEFT_SET_COMPREHENSIONS.toString());
      }
      for (ASTSetComprehensionItem item : ((ASTSetComprehension) expression)
        .getSetComprehensionItemList()) {
        if (!item.isPresentExpression()) {

          Log.error(SD4ComponentTestingError.EXPRESSION_RIGHT_SET_COMPREHENSIONS.toString());
        }
      }
    }
    else if (expression instanceof ASTForallExpression) {
      if (((ASTForallExpression) expression).getInDeclarationList().size() > 1) {
        Log.error(SD4ComponentTestingError.EXPRESSION_FOR_ALL_EXPRESSION.toString());
      }
      handleInDeclaration(((ASTForallExpression) expression).getInDeclaration(0), "ForallExpression");
    }
    else if (expression instanceof ASTExistsExpression) {
      if (((ASTExistsExpression) expression).getInDeclarationList().size() > 1) {
        Log.error(SD4ComponentTestingError.EXPRESSION_EXISTS_EXPRESSION.toString());
      }
      handleInDeclaration(((ASTExistsExpression) expression).getInDeclaration(0), "ExistsExpression");
    }
    else if (expression instanceof ASTIterateExpression) {
      handleInDeclaration(((ASTIterateExpression) expression).getIteration(), "IterateExpression");
    }
    else if (expression instanceof ASTAnyExpression) {
      if (!(((ASTAnyExpression) expression).getExpression() instanceof ASTSetEnumeration ||
        ((ASTAnyExpression) expression).getExpression() instanceof ASTSetComprehension)) {
        Log.error(SD4ComponentTestingError.EXPRESSION_ANY_EXPRESSION.toString());
      }
    }
    else if (expression instanceof ASTOCLAtPreQualification) {
      if (!(((ASTOCLAtPreQualification) expression).getExpression() instanceof ASTNameExpression)) {
        Log.error(SD4ComponentTestingError.EXPRESSION_OCL_AT_PRE_QUALIFICATION.toString());
      }
    }
    else if (expression instanceof ASTOCLArrayQualification) {
      Log.error(SD4ComponentTestingError.EXPRESSION_OCL_ARRAY_QUALIFICATION.toString());
    }
    else if (expression instanceof ASTOCLTransitiveQualification) {
      Log.error(SD4ComponentTestingError.EXPRESSION_OCL_TRANSITIVE_QUALIFICATION.toString());
    }
  }

  protected void handleInDeclaration(ASTInDeclaration inDeclaration, String nodeName) {
    if (!inDeclaration.isPresentExpression()) {
      Log.error(String.format(SD4ComponentTestingError.EXPRESSION_IN_DECLARATION_WITHOUT_EXPRESSIONS.toString(), nodeName));
    }
    if (inDeclaration.getExpression() instanceof ASTSetEnumeration) {
      handleSet((ASTSetEnumeration) inDeclaration.getExpression());
    }
    else if (inDeclaration.getExpression() instanceof ASTSetComprehension) {
      handleSet((ASTSetComprehension) inDeclaration.getExpression());
    }
    else {
      Log.error(String.format(SD4ComponentTestingError.EXPRESSION_IN_DECLARATION.toString(), nodeName));
    }
  }

  protected void handleSet(ASTSetEnumeration set) {
    for (ASTSetCollectionItem item : set.getSetCollectionItemList()) {
      if (item instanceof ASTSetValueRegEx) {
        Log.error(SD4ComponentTestingError.EXPRESSION_IN_DECLARATION_SET_DEFINITIONS.toString());
      }
    }
  }

  protected void handleSet(ASTSetComprehension set) {
    if (!set.getLeft().isPresentGeneratorDeclaration()) {

      Log.error(SD4ComponentTestingError.EXPRESSION_SET_COMPREHENSIONS.toString());
    }
    else {
      if (set.getLeft().getGeneratorDeclaration().getExpression() instanceof ASTSetEnumeration) {
        handleSet((ASTSetEnumeration) set.getLeft().getGeneratorDeclaration().getExpression());
      }
      else {
        Log.error(SD4ComponentTestingError.EXPRESSION_GENERATOR_DECLARATIONS.toString());
      }
    }
  }
}
