// (c) https://github.com/MontiCore/monticore
package types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ocl.setexpressions._ast.ASTSetCollectionItem;
import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.ocl.setexpressions._ast.ASTSetValueItem;
import de.monticore.ocl.setexpressions._visitor.SetExpressionsVisitor;
import de.monticore.ocl.types.check.DeriveSymTypeOfSetExpressions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._ast.ASTSetValueRegEx;
import setdefinitions._visitor.SetDefinitionsVisitor;

import static de.monticore.ocl.types.check.OCLTypeCheck.compatible;

public class DeriveSymTypeOfSetDefinitions extends DeriveSymTypeOfSetExpressions implements SetDefinitionsVisitor {

  private SetDefinitionsVisitor realThis;

  public DeriveSymTypeOfSetDefinitions(){
    this.realThis = this;
  }

  @Override
  public void setRealThis(SetDefinitionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void setRealThis(SetExpressionsVisitor realThis) {
    this.realThis = (SetDefinitionsVisitor) realThis;
  }

  @Override
  public SetDefinitionsVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void traverse(ASTSetEnumeration node) {
    SymTypeExpression result = null;
    SymTypeExpression innerResult = null;
    if(node.isPresentMCType()){
      node.getMCType().accept(getRealThis());
      if(typeCheckResult.isPresentCurrentResult()){
        boolean correct = false;
        for (String s : collections) {
          if (typeCheckResult.getCurrentResult().getTypeInfo().getName().equals(s)) {
            correct = true;
          }
        }
        if (!correct) {
          typeCheckResult.reset();
          Log.error("0xA0298 there must be a type at " + node.getMCType().get_SourcePositionStart());
        }
        else {
          result = SymTypeExpressionFactory.createGenerics(typeCheckResult.getCurrentResult().
                  getTypeInfo().getName(), getScope(node.getEnclosingScope()));
          typeCheckResult.reset();
        }
      }
      else {
        typeCheckResult.reset();
        Log.error("0xA0299 could not determine type of " + node.getMCType().getClass().getName());
      }
    }

    //MCType not present -> collection is "Set" by default
    if (result == null) {
      result = SymTypeExpressionFactory.createGenerics("Set", getScope(node.getEnclosingScope()));
    }

    //check type of elements in set
    for (ASTSetCollectionItem item : node.getSetCollectionItemList()){
      if (item instanceof ASTSetValueItem) {
        for (ASTExpression e : ((ASTSetValueItem) item).getExpressionList()) {
          e.accept(getRealThis());
        }
        if (typeCheckResult.isPresentCurrentResult()) {
          if (innerResult == null) {
            innerResult = typeCheckResult.getCurrentResult();
            typeCheckResult.reset();
          } else if (!compatible(innerResult, typeCheckResult.getCurrentResult())) {
            Log.error("different types in SetEnumeration");
          }
        } else {
          Log.error("Could not determine type of an expression in SetEnumeration");
        }
      }
      else {
        item.accept(getRealThis());
        if (typeCheckResult.isPresentCurrentResult()) {
          if (innerResult == null) {
            innerResult = typeCheckResult.getCurrentResult();
            typeCheckResult.reset();
          } else if (!compatible(innerResult, typeCheckResult.getCurrentResult())) {
            Log.error("different types in SetEnumeration");
          }
        } else {
          Log.error("Could not determine type of a SetValueRange in SetEnumeration");
        }
      }
    }

    ((SymTypeOfGenerics) result).addArgument(innerResult);
    typeCheckResult.setCurrentResult(result);
  }

  @Override
  public void traverse(ASTSetValueRange node){
    SymTypeExpression left = this.acceptThisAndReturnSymTypeExpressionOrLogError(node.getLowerBound(), "0xA0311");
    SymTypeExpression right = this.acceptThisAndReturnSymTypeExpressionOrLogError(node.getUpperBound(), "0xA0312");
    if(node.isPresentStepsize()){
      SymTypeExpression stepSize = this.acceptThisAndReturnSymTypeExpressionOrLogError(node.getStepsize(), "0xA0312");
      if(!this.isIntegralType(stepSize)){
        Log.error("stepsize of SetValueRange is not an integral type, but has to be one");
      }
    }
    if (!this.isIntegralType(left) || !this.isIntegralType(right)) {
      Log.error("bounds in SetValueRange are not integral types, but have to be");
    }

    this.typeCheckResult.setCurrentResult(left);
  }

  @Override
  public void traverse(ASTSetValueRegEx node){
    //String is the only type of RegEx which is currently supported in this non-terminal
    this.typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeObject("String", node.getEnclosingScope()));
  }
}
