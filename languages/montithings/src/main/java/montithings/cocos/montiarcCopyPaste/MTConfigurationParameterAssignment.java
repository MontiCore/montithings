// (c) https://github.com/MontiCore/monticore
package montithings.cocos.montiarcCopyPaste;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentInstance;
import arcbasis._cocos.ConfigurationParameterAssignment;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis.check.ArcBasisDerive;
import arcbasis.check.FullSynthesizeSymTypeFromMCBasicTypes;
import arcbasis.util.ArcError;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols._ast.ASTVariable;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

public class MTConfigurationParameterAssignment extends ConfigurationParameterAssignment {
  protected final MontiThingsTypeCheck typeChecker;

  /**
   * Creates this coco with an MontiThingsTypeCheck, combined with {@link ArcBasisDerive} to check whether instantiation
   * arguments match a component types signature.
   */
  public MTConfigurationParameterAssignment() {
    this(new MontiThingsTypeCheck(
      new SynthesizeSymTypeFromMontiThings(),
      new DeriveSymTypeOfMontiThingsCombine()
    ));
  }

  /**
   * Creates this coco with a custom MontiThingsTypeCheck to use to check if instantiation arguments match a component types
   * signature.
   */
  public MTConfigurationParameterAssignment(@NotNull MontiThingsTypeCheck typeChecker) {
    this.typeChecker = checkNotNull(typeChecker);
  }

  /**
   * Creates this coco with a custom IDerive to use to check if instantiation arguments match a component types
   * signature.
   */
  public MTConfigurationParameterAssignment(@NotNull IDerive deriverFromExpr) {
    this(new MontiThingsTypeCheck(new FullSynthesizeSymTypeFromMCBasicTypes(),
      checkNotNull(deriverFromExpr)));
  }

  @Override
  public void check(ASTComponentInstance node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(),
      "Could not perform coco check '%s'. Perhaps you missed the " +
        "symbol table creation.", this.getClass().getSimpleName());
    Preconditions.checkArgument(node.getSymbol().getType() != null);

    ComponentInstanceSymbol compInstance = node.getSymbol();
    ComponentTypeSymbol typeOfCompInstance = compInstance.getType();

    List<ASTExpression> paramBindings = compInstance.getArguments();
    List<SymTypeExpression> bindingSignature =
      paramBindings.stream()
        .map(typeChecker::typeOf)
        .collect(toList());

    List<VariableSymbol> configParamsOfType = typeOfCompInstance.getParameters();
    List<SymTypeExpression> signatureOfCompType =
      configParamsOfType.stream()
        .map(VariableSymbol::getType)
        .collect(toList());

    // checking that not too many arguments were provided during instantiation
    if (bindingSignature.size() > signatureOfCompType.size()) {
      this.logCocoViolation(bindingSignature, compInstance);
      return; // to terminate when fail-fast of logger is off (e.g. during tests)
    }

    // checking that configuration parameters are assignable from arguments
    for (int i = 0; i < bindingSignature.size(); i++) {
      if (!MontiThingsTypeCheck.compatible(signatureOfCompType.get(i), bindingSignature.get(i))) {
        this.logCocoViolation(bindingSignature, compInstance);
        return; // to terminate when fail-fast of logger is off (e.g. during tests)
      }
    }

    // checking that all parameters left have default values
    if (!configParamsOfType.stream()
      .skip(bindingSignature.size())
      .map(VariableSymbol::getAstNode)
      .peek(astVar -> assertVarIsArcParameter(astVar, typeOfCompInstance))
      .map(astVar -> (ASTArcParameter) astVar)
      .allMatch(ASTArcParameter::isPresentDefault)) {
      this.logCocoViolation(bindingSignature, compInstance);
      return; // to terminate when fail-fast of logger is off (e.g. during tests)
    }
  }

  protected void assertVarIsArcParameter(ASTVariable configParam, ComponentTypeSymbol fromType) {
    Preconditions
      .checkArgument(configParam instanceof ASTArcParameter, "Could not check coco '%s', because " +
          "configuration parameter '%s' of component type '%s' is not of type '%s",
        this.getClass().getSimpleName(),
        configParam.getName(), fromType.getFullName(), ASTArcParameter.class.getSimpleName());
  }

  protected void logCocoViolation(
    List<SymTypeExpression> bindingSignature, ComponentInstanceSymbol compInstance) {
    Log.error(ArcError.CONFIG_PARAMETER_BINDING.format(compInstance.getType().getFullName(),
      printSignature(bindingSignature), compInstance.getFullName()));
  }

  protected String printSignature(List<SymTypeExpression> signature) {
    List<String> signatureParts = signature.stream().map(SymTypeExpression::print)
      .collect(toList());
    return "(" + String.join(", ", signatureParts) + ")";
  }
}
