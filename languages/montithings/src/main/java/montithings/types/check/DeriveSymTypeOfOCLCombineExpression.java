// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.ocl.types.check.DeriveSymTypeOfOCLCombineExpressions;
import types.check.DeriveSymTypeOfSetDefinitions;

public class DeriveSymTypeOfOCLCombineExpression extends DeriveSymTypeOfOCLCombineExpressions {

  private DeriveSymTypeOfSetDefinitions deriveSymTypeOfSetDefinitions;

  public DeriveSymTypeOfOCLCombineExpression(){
    super();
    this.setRealThis(this);
  }

  @Override
  public void init(){
    super.init();
    this.deriveSymTypeOfSetDefinitions = new DeriveSymTypeOfSetDefinitions();
    this.deriveSymTypeOfSetDefinitions.setTypeCheckResult(this.getTypeCheckResult());
    this.setSetExpressionsVisitor(this.deriveSymTypeOfSetDefinitions);
  }
}
