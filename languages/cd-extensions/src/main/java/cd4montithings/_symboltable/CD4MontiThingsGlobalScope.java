package cd4montithings._symboltable;

import de.monticore.cd._symboltable.BuiltInTypes;
import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.io.paths.ModelPath;

import java.util.Optional;
import java.util.Set;

public class CD4MontiThingsGlobalScope extends CD4MontiThingsGlobalScopeTOP {
  public static final String EXTENSION = "cd";
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4MontiThingsGlobalScope(){
    super();
    setSymbolTableHelper(new CDSymbolTableHelper(new DeriveSymTypeOfCD4Code()));
  }

  public CD4MontiThingsGlobalScope(ModelPath modelPath) {
    super(modelPath, EXTENSION);
    setSymbolTableHelper(new CDSymbolTableHelper(new DeriveSymTypeOfCD4Code()));
  }

  public CD4MontiThingsGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
    setSymbolTableHelper(new CDSymbolTableHelper(new DeriveSymTypeOfCD4Code()));
  }

  @Override
  public CD4MontiThingsGlobalScope getRealThis() {
    return this;
  }

  public void setSymbolTableHelper(CDSymbolTableHelper symbolTableHelper) {
    this.symbolTableHelper = symbolTableHelper;
  }

  public CDSymbolTableHelper getSymbolTableHelper() {
    return symbolTableHelper;
  }

  public Set<String> calculateModelNamesSimple(String qName) {
    return CDSymbolTableHelper.calculateModelNamesSimple(qName, symbolTableHelper);
  }

  public void addBuiltInTypes() {
    BuiltInTypes.addBuiltInTypes(this, Optional.of(new DeriveSymTypeOfCD4Code()));
  }

  @Override
  public Set<String> calculateModelNamesForCDType(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForOOType(String name) {
    return calculateModelNamesForCDType(name);
  }

  @Override
  public Set<String> calculateModelNamesForType(String name) {
    return calculateModelNamesForOOType(name);
  }

  @Override
  public Set<String> calculateModelNamesForField(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForVariable(String name) {
    return calculateModelNamesForField(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDMethodSignature(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForMethod(String name) {
    return calculateModelNamesForCDMethodSignature(name);
  }

  @Override
  public Set<String> calculateModelNamesForFunction(String name) {
    return calculateModelNamesForMethod(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDAssociation(String name) {
    return calculateModelNamesSimple(name);
  }

  @Override
  public Set<String> calculateModelNamesForCDRole(String name) {
    return calculateModelNamesSimple(name);
  }
}
