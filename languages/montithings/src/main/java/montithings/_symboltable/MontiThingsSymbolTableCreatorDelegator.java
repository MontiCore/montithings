// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;

public class MontiThingsSymbolTableCreatorDelegator extends MontiThingsSymbolTableCreatorDelegatorTOP {

  public MontiThingsSymbolTableCreatorDelegator() {
    super();
    setOCLExpressionsVisitor(new OCLExpressionsSTCForMontiThingsTemp(scopeStack));
    setSetExpressionsVisitor(new SetExpressionsSTCForMontiThingsTemp(scopeStack));
  }

  public MontiThingsSymbolTableCreatorDelegator(montithings._symboltable.IMontiThingsGlobalScope globalScope) {
    super(globalScope);
    ((ArcBasisSTCForMontiThings) getArcBasisVisitor().get()).setTypeVisitor(new SIUnitTypes4ComputingPrettyPrinter(new IndentPrinter()));
    setOCLExpressionsVisitor(new OCLExpressionsSTCForMontiThingsTemp(scopeStack));
    setSetExpressionsVisitor(new SetExpressionsSTCForMontiThingsTemp(scopeStack));
  }
}
