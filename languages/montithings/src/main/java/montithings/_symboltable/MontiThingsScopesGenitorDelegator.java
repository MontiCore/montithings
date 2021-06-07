// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunittypes4computing.prettyprint.SIUnitTypes4ComputingPrettyPrinter;

public class MontiThingsScopesGenitorDelegator extends MontiThingsScopesGenitorDelegatorTOP {

  ArcBasisSTCForMontiThings arcBasis;
  OCLExpressionsSTCForMontiThingsTemp ocl;
  SetExpressionsSTCForMontiThingsTemp setExpr;


  public MontiThingsScopesGenitorDelegator() {
    super();
    arcBasis = new ArcBasisSTCForMontiThings();
    traverser.setArcBasisHandler(arcBasis);
    traverser.add4ArcBasis(arcBasis);

    ocl = new OCLExpressionsSTCForMontiThingsTemp();
    traverser.setOCLExpressionsHandler(ocl);
    traverser.add4OCLExpressions(ocl);

    setExpr = new SetExpressionsSTCForMontiThingsTemp();
    traverser.setSetExpressionsHandler(setExpr);
    traverser.add4SetExpressions(setExpr);
  }
}
