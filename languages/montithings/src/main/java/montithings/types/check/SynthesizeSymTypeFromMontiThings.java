// (c) https://github.com/MontiCore/monticore
package montithings.types.check;

import de.monticore.types.check.*;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;

import java.util.Optional;

public class SynthesizeSymTypeFromMontiThings
  implements ISynthesize {

  protected MontiThingsTraverser traverser;

  private SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes;

  private SynthesizeSymTypeFromMCSimpleGenericTypes symTypeFromMCSimpleGenericTypes;

  private SynthesizeSymTypeFromSIUnitTypes4Math symTypeFromSIUnitTypes4Math;

  private SynthesizeSymTypeFromSIUnitTypes4Computing symTypeFromSIUnitTypes4Computing;

  public void init() {
    traverser = MontiThingsMill.traverser();

    symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    symTypeFromSIUnitTypes4Math = new SynthesizeSymTypeFromSIUnitTypes4Math();
    symTypeFromSIUnitTypes4Computing = new SynthesizeSymTypeFromSIUnitTypes4Computing();

    setTypeCheckResult(new TypeCheckResult());

    traverser.setMCBasicTypesHandler(symTypeFromMCBasicTypes);
    traverser.add4MCBasicTypes(symTypeFromMCBasicTypes);
    traverser.setMCSimpleGenericTypesHandler(symTypeFromMCSimpleGenericTypes);
    traverser.add4MCSimpleGenericTypes(symTypeFromMCSimpleGenericTypes);
    traverser.setSIUnitTypes4MathHandler(symTypeFromSIUnitTypes4Math);
    traverser.setSIUnitTypes4ComputingHandler(symTypeFromSIUnitTypes4Computing);
    setTypeCheckResult(typeCheckResult);
  }

  @Override public MontiThingsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MontiThingsTraverser traverser) {
    this.traverser = traverser;
  }

  public SynthesizeSymTypeFromMontiThings() {
    init();
  }

  public TypeCheckResult typeCheckResult;

  public Optional<SymTypeExpression> getResult() {
    if (typeCheckResult.isPresentCurrentResult()) {
      return Optional.of(typeCheckResult.getCurrentResult());
    } else {
      return Optional.empty();
    }
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult) {
    this.typeCheckResult = typeCheckResult;
    this.symTypeFromMCBasicTypes.setTypeCheckResult(typeCheckResult);
    this.symTypeFromMCSimpleGenericTypes.setTypeCheckResult(typeCheckResult);
    this.symTypeFromSIUnitTypes4Math.setTypeCheckResult(typeCheckResult);
    this.symTypeFromSIUnitTypes4Computing.setTypeCheckResult(typeCheckResult);
  }

}
