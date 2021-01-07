package montithings._symboltable;

import arcbasis._symboltable.ArcBasisSymbolTableCreator;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;
import de.monticore.siunittypes4math.prettyprint.SIUnitTypes4MathPrettyPrinter;
import de.monticore.types.check.SynthesizeSymTypeFromSIUnitTypes4Computing;

public class MontiThingsSymbolTableCreatorDelegator extends MontiThingsSymbolTableCreatorDelegatorTOP {

  public MontiThingsSymbolTableCreatorDelegator(){
    super();
  }

  public MontiThingsSymbolTableCreatorDelegator(montithings._symboltable.IMontiThingsGlobalScope globalScope) {
    super(globalScope);
    ((ArcBasisSTCForMontiThings)getArcBasisVisitor().get()).setTypeVisitor(
            new SIUnitTypes4ComputingPrettyPrinter(new IndentPrinter()));
  }
}
