// (c) https://github.com/MontiCore/monticore
package types.check;

import de.monticore.ocl.setexpressions._ast.ASTSetEnumeration;
import de.monticore.ocl.types.check.DeriveSymTypeOfSetExpressions;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;
import setdefinitions._ast.ASTSetValueRange;
import setdefinitions._ast.ASTSetValueRegEx;
import setdefinitions._visitor.SetDefinitionsVisitor;

import java.util.Iterator;

public class DeriveSymTypeOfSetDefinitions extends DeriveSymTypeOfSetExpressions implements SetDefinitionsVisitor {

  private SetDefinitionsVisitor realThis;

  public DeriveSymTypeOfSetDefinitions(){
    realThis = this;
  }

  @Override
  public void setRealThis(SetDefinitionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public SetDefinitionsVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void traverse(ASTSetEnumeration node) {
    SymTypeExpression result = null;
    SymTypeExpression innerResult = null;
    if (node.isPresentMCType()) {
      node.getMCType().accept(this.getRealThis());
      if (this.typeCheckResult.isPresentCurrentResult()) {
        boolean correct = false;
        Iterator var6 = this.collections.iterator();

        while(var6.hasNext()) {
          String s = (String)var6.next();
          if (this.typeCheckResult.getCurrentResult().getTypeInfo().getName().equals(s)) {
            correct = true;
          }
        }

        if (!correct) {
          this.typeCheckResult.reset();
          Log.error("0xA0298 there must be a type at " + node.getMCType().get_SourcePositionStart());
        } else {
          result = SymTypeExpressionFactory.createGenerics(this.typeCheckResult.getCurrentResult().getTypeInfo().getName(), this.getScope(node.getEnclosingScope()));
          this.typeCheckResult.reset();
        }
      } else {
        this.typeCheckResult.reset();
        Log.error("0xA0299 could not determine type of " + node.getMCType().getClass().getName());
      }
    }

    if (result == null) {
      result = SymTypeExpressionFactory.createGenerics("Set", this.getScope(node.getEnclosingScope()));
    }

    //TODO: better innerResult calculation
    innerResult = SymTypeExpressionFactory.createTypeConstant("int");

    ((SymTypeOfGenerics)result).addArgument(innerResult);
    this.typeCheckResult.setCurrentResult(result);
    return;
  }

  @Override
  public void traverse(ASTSetValueRange node){
    SymTypeExpression left = this.acceptThisAndReturnSymTypeExpressionOrLogError(node.getLowerBound(), "0xA0311");
    SymTypeExpression right = this.acceptThisAndReturnSymTypeExpressionOrLogError(node.getUpperBound(), "0xA0312");
    SymTypeExpression stepSize = this.acceptThisAndReturnSymTypeExpressionOrLogError(node.getStepsize(), "0xA0312");
    if (!this.isIntegralType(left) || !this.isIntegralType(right) || !this.isIntegralType(stepSize)) {
      Log.error("bounds/stepsize in SetValueRange are not integral types, but have to be");
    }

    this.typeCheckResult.setCurrentResult(left);
  }

  @Override
  public void traverse(ASTSetValueRegEx node){
    //TODO: implement type of SetValueRegEx properly
    this.typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeConstant("int"));
  }
}
