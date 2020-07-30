package montithings.generator.codegen;

import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentTypeSymbol;
import bindings._ast.ASTBindingRule;
import cdlangextension._symboltable.CDLangExtensionScope;

import java.util.*;

/**
 * Bundle of parameters for montithings2cpp generator.
 *
 * @author Julian Krebber
 * @version 5.0.2
 * @revision (see commit history)
 * @since 5.0.2
 */
public class ConfigParams {
  public enum TargetPlatform {
    GENERIC,
    DSA_VCG,
    ARDUINO
  }

  /**
   * Defines how the architecture is splitted in different binaries
   * OFF = No splitting, create a single binary containing everything
   * LOCAL = Deploy on a single machine
   * DISTRIBUTED = Deploy on multiple machines
   */
  public enum SplittingMode {
    OFF,
    LOCAL,
    DISTRIBUTED
  }

  /** property for target platform */
  private TargetPlatform targetPlatform = TargetPlatform.GENERIC;

  private SplittingMode splittingMode = SplittingMode.OFF;

  private Set<ASTBindingRule> componentBindings = new HashSet<>();

  private CDLangExtensionScope cdLangExtensionScope;

  private Map<String, List<String>> componentPortMap;

  public Map<String, List<String>> getComponentPortMap() {
    return componentPortMap;
  }

  public void setComponentPortMap(Map<String, List<String>> componentPortMap) {
    this.componentPortMap = componentPortMap;
  }

  public TargetPlatform getTargetPlatform() {
    return targetPlatform;
  }

  public void setTargetPlatform(TargetPlatform targetPlatform) {
    this.targetPlatform = targetPlatform;
  }

  public Set<ASTBindingRule> getComponentBindings() {
    return componentBindings;
  }

  public void setComponentBindings(Set<ASTBindingRule> componentBindings) {
    this.componentBindings = componentBindings;
  }

  public Optional<ComponentTypeSymbol> getBinding(ComponentTypeSymbol componentType){
    for(ASTBindingRule binding : componentBindings){
      if(binding.getInterfaceComponentSymbol().equals(componentType)){
        return Optional.of(binding.getImplementationComponentSymbol());
      }
    }
    return Optional.empty();
  }

  public Optional<ASTComponentType> getBinding(ASTComponentType componentType){
    for(ASTBindingRule binding : componentBindings){
      if(binding.getInterfaceComponentDefinition().equals(componentType)){
        return Optional.of(binding.getImplementationComponentDefinition());
      }
    }
    return Optional.empty();
  }

  public boolean isImplementation(ASTComponentType componentType){
    for(ASTBindingRule binding : componentBindings){
      if(binding.getImplementationComponentDefinition().equals(componentType)){
        return true;
      }
    }
    return false;
  }

  public boolean isImplementation(ComponentTypeSymbol componentType){
    for(ASTBindingRule binding : componentBindings){
      if(binding.getImplementationComponentSymbol().equals(componentType)){
        return true;
      }
    }
    return false;
  }

  public CDLangExtensionScope getCdLangExtensionScope() {
    return cdLangExtensionScope;
  }

  public void setCdLangExtensionScope(CDLangExtensionScope cdLangExtensionScope) {
    this.cdLangExtensionScope = cdLangExtensionScope;
  }

  public SplittingMode getSplittingMode() {
    return splittingMode;
  }

  public void setSplittingMode(SplittingMode splittingMode) {
    this.splittingMode = splittingMode;
  }
}
