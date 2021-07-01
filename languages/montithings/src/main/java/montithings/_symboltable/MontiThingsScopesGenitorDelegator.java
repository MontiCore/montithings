// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

public class MontiThingsScopesGenitorDelegator extends MontiThingsScopesGenitorDelegatorTOP {

  ArcBasisSTCForMontiThings arcBasis;
  OCLExpressionsSTCForMontiThingsTemp ocl;
  SetExpressionsSTCForMontiThingsTemp setExpr;


  public MontiThingsScopesGenitorDelegator() {
    super();
    traverser.getArcBasisVisitorList().clear();
    arcBasis = new ArcBasisSTCForMontiThings();
    arcBasis.setScopeStack(scopeStack);
    traverser.setArcBasisHandler(arcBasis);
    traverser.add4ArcBasis(arcBasis);


    traverser.getOCLExpressionsVisitorList().clear();
    ocl = new OCLExpressionsSTCForMontiThingsTemp();
    ocl.setScopeStack(scopeStack);
    traverser.setOCLExpressionsHandler(ocl);
    traverser.add4OCLExpressions(ocl);

    traverser.getSetExpressionsVisitorList().clear();
    setExpr = new SetExpressionsSTCForMontiThingsTemp();
    setExpr.setScopeStack(scopeStack);
    traverser.setSetExpressionsHandler(setExpr);
    traverser.add4SetExpressions(setExpr);
  }
}
