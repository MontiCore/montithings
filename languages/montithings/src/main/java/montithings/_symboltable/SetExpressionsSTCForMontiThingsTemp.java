// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.ocl.setexpressions._symboltable.ISetExpressionsScope;
import de.monticore.ocl.setexpressions._symboltable.SetExpressionsScopesGenitor;
import montithings.MontiThingsMill;

import java.util.Deque;

public class SetExpressionsSTCForMontiThingsTemp extends SetExpressionsScopesGenitor {

  public IMontiThingsScope createScope(boolean shadowing) {
    IMontiThingsScope scope = MontiThingsMill.scope();
    scope.setShadowing(shadowing);
    return scope;
  }
}

