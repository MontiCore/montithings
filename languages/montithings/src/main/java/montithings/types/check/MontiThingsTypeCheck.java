// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.ocl.types.check.OCLTypeCheck;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeConstant;
import de.monticore.types.check.SymTypeExpression;

public class MontiThingsTypeCheck extends OCLTypeCheck {

  protected boolean condition;

  public MontiThingsTypeCheck(ISynthesize synthesizeSymType, IDerive iTypesCalculator) {
    super(synthesizeSymType, iTypesCalculator);
    condition = false;
  }


  public void setCondition(boolean b) {
    if (iDerive instanceof DeriveSymTypeOfMontiThingsCombine) {
      DeriveSymTypeOfMontiThingsCombine mTCalculator = (DeriveSymTypeOfMontiThingsCombine) iDerive;
      if (mTCalculator.getTraverser().getMontiThingsHandler().isPresent() &&
        mTCalculator.getTraverser().getMontiThingsHandler().get() instanceof DeriveSymTypeOfMontiThings) {
        DeriveSymTypeOfMontiThings.setCondition(b);
      }
    }
  }

  public static boolean castCompatible(SymTypeExpression left, SymTypeExpression right) {
    if (left.isTypeConstant() && right.isTypeConstant()) {
      SymTypeConstant leftType = (SymTypeConstant) left;
      SymTypeConstant rightType = (SymTypeConstant) right;

      if (leftType.isNumericType() && rightType.isNumericType()) {
        return true;
      }

    }

    // In any other case use the existing type checks
    return OCLTypeCheck.compatible(left, right);
  }
}
