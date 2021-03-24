// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.ocl.setexpressions._symboltable.ISetExpressionsScope;

import java.util.Deque;

public class SetExpressionsSTCForMontiThingsTemp extends SetExpressionsSymbolTableCreator {

  public SetExpressionsSTCForMontiThingsTemp(Deque<? extends ISetExpressionsScope> scopeStack) {
    super(scopeStack);
  }

  public montithings._symboltable.IMontiThingsScope createScope(boolean shadowing) {
    montithings._symboltable.IMontiThingsScope scope = montithings.MontiThingsMill.montiThingsScopeBuilder().build();
    scope.setShadowing(shadowing);
    return scope;
  }
}

