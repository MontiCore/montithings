package montithings.types.check;

import de.monticore.types.check.*;
import montithings._visitor.MontiThingsDelegatorVisitor;
import montithings._visitor.MontiThingsVisitor;

import java.util.Optional;

public class SynthesizeSymTypeFromMontiThings extends MontiThingsDelegatorVisitor implements ISynthesize {

  private SynthesizeSymTypeFromMCBasicTypes symTypeFromMCBasicTypes;
  private SynthesizeSymTypeFromSIUnitTypes4Math symTypeFromSIUnitTypes4Math;
  private SynthesizeSymTypeFromSIUnitTypes4Computing symTypeFromSIUnitTypes4Computing;

  public void init() {
    typeCheckResult = new TypeCheckResult();

    symTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    symTypeFromSIUnitTypes4Math = new SynthesizeSymTypeFromSIUnitTypes4Math();
    symTypeFromSIUnitTypes4Computing = new SynthesizeSymTypeFromSIUnitTypes4Computing();

    symTypeFromMCBasicTypes.setTypeCheckResult(typeCheckResult);
    symTypeFromSIUnitTypes4Math.setTypeCheckResult(typeCheckResult);
    symTypeFromSIUnitTypes4Computing.setTypeCheckResult(typeCheckResult);

    setMCBasicTypesVisitor(symTypeFromMCBasicTypes);
    setSIUnitTypes4MathVisitor(symTypeFromSIUnitTypes4Math);
    setSIUnitTypes4ComputingVisitor(symTypeFromSIUnitTypes4Computing);
    setMontiThingsVisitor(new MontiThingsDelegatorVisitor());
    setTypeCheckResult(typeCheckResult);
  }

  public SynthesizeSymTypeFromMontiThings() {
    init();
  }

  MontiThingsVisitor realThis = this;

  @Override
  public void setRealThis(MontiThingsVisitor realThis) {
    this.realThis = realThis;
  }


  public TypeCheckResult typeCheckResult;

  public Optional<SymTypeExpression> getResult() {
    return Optional.of(typeCheckResult.getCurrentResult());
  }

  public void setTypeCheckResult(TypeCheckResult typeCheckResult){
    this.typeCheckResult = typeCheckResult;
    this.symTypeFromMCBasicTypes.setTypeCheckResult(typeCheckResult);
    this.symTypeFromSIUnitTypes4Math.setTypeCheckResult(typeCheckResult);
    this.symTypeFromSIUnitTypes4Computing.setTypeCheckResult(typeCheckResult);
  }

}
