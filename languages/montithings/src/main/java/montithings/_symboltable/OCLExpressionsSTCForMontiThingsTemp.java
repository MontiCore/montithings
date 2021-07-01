// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.ocl.oclexpressions._symboltable.OCLExpressionsScopesGenitor;
import montithings.MontiThingsMill;

public class OCLExpressionsSTCForMontiThingsTemp extends OCLExpressionsScopesGenitor {
  @Override
  public IMontiThingsScope createScope(boolean shadowing) {
    IMontiThingsScope scope = MontiThingsMill.scope();
    scope.setShadowing(shadowing);
    return scope;
  }
}
