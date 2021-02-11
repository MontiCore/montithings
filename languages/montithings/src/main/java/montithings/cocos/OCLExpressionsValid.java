package montithings.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisASTExpressionCoCo;
import de.monticore.ocl.oclexpressions._ast.*;
import de.monticore.ocl.setexpressions._ast.*;
import de.se_rwth.commons.logging.Log;
import setdefinitions._ast.ASTSetValueRegEx;

public class OCLExpressionsValid implements ExpressionsBasisASTExpressionCoCo {

  @Override
  public void check(ASTExpression node) {
    if(node instanceof ASTSetAndExpression){
      Log.error("0xMT820 SetAndExpression is not supported.");
    }
    else if(node instanceof ASTSetOrExpression){
      Log.error("0xMT821 SetOrExpression is not supported.");
    }
    else if(node instanceof ASTSetUnionExpression){
      Log.error("0xMT822 SetUnionExpression is not supported.");
    }
    else if(node instanceof ASTSetIntersectionExpression){
      Log.error("0xMT823 SetIntersectionExpression is not supported.");
    }
    else if(node instanceof ASTSetComprehension){
      if(((ASTSetComprehension) node).getLeft().isPresentExpression()){
        Log.error("0xMT824 Expressions at the left side of SetComprehensions are not supported");
      }
      for (ASTSetComprehensionItem item : ((ASTSetComprehension) node).getSetComprehensionItemList()){
        if (!item.isPresentExpression()){
          Log.error("0xMT825 Only expressions are supported at the right side of set comprehensions");
        }
      }
    }
    else if (node instanceof ASTForallExpression){
      if(((ASTForallExpression) node).getInDeclarationList().size() > 1){
        Log.error("0xMT826 Only one InDeclaration is supported for every ForallExpression");
      }
      handleInDeclaration(((ASTForallExpression) node).getInDeclaration(0), "ForallExpression");
    }
    else if (node instanceof ASTExistsExpression){
      if(((ASTExistsExpression) node).getInDeclarationList().size() > 1){
        Log.error("0xMT827 Only one InDeclaration is supported for every ExistsExpression");
      }
      handleInDeclaration(((ASTExistsExpression) node).getInDeclaration(0), "ExistsExpression");
    }
    else if (node instanceof ASTIterateExpression){
      handleInDeclaration(((ASTIterateExpression) node).getIteration(), "IterateExpression");
    }
    else if (node instanceof ASTAnyExpression){
      if(!(((ASTAnyExpression) node).getExpression() instanceof ASTSetEnumeration ||
              ((ASTAnyExpression) node).getExpression() instanceof ASTSetComprehension)){
        Log.error("0xMT828 Only SetEnumerations or SetComprehensions are allowed in AnyExpressions");
      }
    }
    else if (node instanceof ASTOCLAtPreQualification){
      if(!(((ASTOCLAtPreQualification) node).getExpression() instanceof ASTNameExpression)) {
        Log.error("0xMT829 OCLAtPreQualification can only be applied to variables of components");
      }
    }
    else if (node instanceof ASTOCLArrayQualification){
      Log.error("0xMT830 OCLArrayQualification is not supported");
    }
    else if(node instanceof ASTOCLTransitiveQualification){
      Log.error("0xMT831 OCLTransitiveQualification is not supported");
    }
  }

  public void handleInDeclaration(ASTInDeclaration inDeclaration, String nodeName){
    if (!inDeclaration.isPresentExpression()){
      Log.error("0xMT832 InDeclarations without Expressions in " + nodeName + " are not supported");
    }
    if(inDeclaration.getExpression() instanceof ASTSetEnumeration){
      handleSet((ASTSetEnumeration) inDeclaration.getExpression());
    }
    else if(inDeclaration.getExpression() instanceof ASTSetComprehension){
      handleSet((ASTSetComprehension) inDeclaration.getExpression());
    }
    else {
      Log.error("0xMT833 Only SetEnumerations and SetComprehensions are supported " +
              "as Expressions in InDeclarations of " + nodeName);
    }
  }

  private void handleSet(ASTSetEnumeration set) {
    for (ASTSetCollectionItem item : set.getSetCollectionItemList()){
      if (item instanceof ASTSetValueRegEx){
        Log.error("0xMT834 only SetValueItems and SetValueRanges are supported in InDeclaration SetDefinitions");
      }
    }
  }

  private void handleSet(ASTSetComprehension set) {
    if (!set.getLeft().isPresentGeneratorDeclaration()){
      Log.error("0xMT835 SetComprehensions in InDeclarations are only supported if the left side is a generator declaration");
    }
    else {
      if (set.getLeft().getGeneratorDeclaration().getExpression() instanceof ASTSetEnumeration){
        handleSet((ASTSetEnumeration) set.getLeft().getGeneratorDeclaration().getExpression());
      }
      else {
        Log.error("0xMT836 Set building expressions other than SetEnumerations are " +
                "not supported in GeneratorDeclarations of SetComprehensions");
      }
    }
  }
}