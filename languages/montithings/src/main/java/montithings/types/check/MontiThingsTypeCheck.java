// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.ocl.types.check.OCLTypeCheck;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.ITypesCalculator;

public class MontiThingsTypeCheck extends OCLTypeCheck {

  protected boolean condition;

  public MontiThingsTypeCheck(ISynthesize synthesizeSymType, ITypesCalculator iTypesCalculator) {
    super(synthesizeSymType, iTypesCalculator);
    condition = false;
  }

  public MontiThingsTypeCheck(ISynthesize synthesizeSymType) {
    super(synthesizeSymType);
    condition = false;
  }

  public MontiThingsTypeCheck(ITypesCalculator iTypesCalculator) {
    super(iTypesCalculator);
    condition = false;
  }

  public void setCondition(boolean b){
    if(iTypesCalculator instanceof DeriveSymTypeOfMontiThingsCombine){
      DeriveSymTypeOfMontiThingsCombine mTCalculator = (DeriveSymTypeOfMontiThingsCombine) iTypesCalculator;
      if(mTCalculator.getMontiThingsVisitor().isPresent() &&
        mTCalculator.getMontiThingsVisitor().get() instanceof DeriveSymTypeOfMontiThings){
        DeriveSymTypeOfMontiThings.setCondition(b);
      }
    }
  }
}
