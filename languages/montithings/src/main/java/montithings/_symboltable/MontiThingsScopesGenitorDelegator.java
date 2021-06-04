// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;

public class MontiThingsScopesGenitorDelegator extends MontiThingsScopesGenitorDelegatorTOP {

  public MontiThingsScopesGenitorDelegator() {
    super();
    traverser.setOCLExpressionsHandler(new OCLExpressionsSTCForMontiThingsTemp(scopeStack));
    traverser.setSetExpressionsHandler(new SetExpressionsSTCForMontiThingsTemp(scopeStack));
  }

  public MontiThingsScopesGenitorDelegator(montithings._symboltable.IMontiThingsGlobalScope globalScope) {
    super(globalScope);
    ((ArcBasisSTCForMontiThings) getArcBasisVisitor().get()).setTypeVisitor(new SIUnitTypes4ComputingPrettyPrinter(new IndentPrinter()));
    traverser.setOCLExpressionsHandler(new OCLExpressionsSTCForMontiThingsTemp(scopeStack));
    traverser.setSetExpressionsHandler(new SetExpressionsSTCForMontiThingsTemp(scopeStack));
  }
}
