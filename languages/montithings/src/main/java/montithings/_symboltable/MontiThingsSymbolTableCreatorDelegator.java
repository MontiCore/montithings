package montithings._symboltable;

import de.monticore.ocl.oclexpressions._symboltable.OCLExpressionsSymbolTableCreator;

import montithings.types.check.DeriveSymTypeOfOCLCombineExpression;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;

public class MontiThingsSymbolTableCreatorDelegator extends MontiThingsSymbolTableCreatorDelegatorTOP {

  public MontiThingsSymbolTableCreatorDelegator(){
    super();
    ((OCLExpressionsSymbolTableCreator) getOCLExpressionsVisitor().get()).setTypeVisitor(new DeriveSymTypeOfOCLCombineExpression());
  }

  public MontiThingsSymbolTableCreatorDelegator(montithings._symboltable.IMontiThingsGlobalScope globalScope) {
    super(globalScope);
    ((ArcBasisSTCForMontiThings)getArcBasisVisitor().get()).setTypeVisitor(
            new SIUnitTypes4ComputingPrettyPrinter(new IndentPrinter()));
    ((OCLExpressionsSymbolTableCreator) getOCLExpressionsVisitor().get()).setTypeVisitor(new DeriveSymTypeOfOCLCombineExpression());
  }
}
