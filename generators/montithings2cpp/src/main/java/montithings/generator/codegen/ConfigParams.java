package montithings.generator.codegen;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import bindings._ast.ASTBindingRule;
import cdlangextension._symboltable.CDLangExtensionScope;
import montithings.generator.data.PortMap;

import java.util.*;

/**
 * Bundle of parameters for montithings2cpp generator.
 *
 * @author Julian Krebber
 * @since 5.0.2
 */
public class ConfigParams {
  public enum TargetPlatform {
    GENERIC("GENERIC"),
    DSA_VCG("DSA_VCG"), // based on dev-docker.sh and docker.dsa-ac.de:20001/dev-l06
    ARDUINO("ARDUINO"),
    DSA_LAB("DSA_LAB"); // connected cars lab, based on docker.dsa-ac.de:20001/dev-l06-customer

    String name;

    TargetPlatform(String name) {
      this.name = name;
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return this.name;
    }
  }

  /**
   * Defines how the architecture is splitted in different binaries
   * OFF = No splitting, create a single binary containing everything
   * LOCAL = Deploy on a single machine
   * DISTRIBUTED = Deploy on multiple machines
   */
  public enum SplittingMode {
    OFF("OFF"),
    LOCAL("LOCAL"),
    DISTRIBUTED("DISTRIBUTED");

    String name;

    SplittingMode(String name) {
      this.name = name;
    }

    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
      return this.name;
    }
  }

  /** property for target platform */
  private TargetPlatform targetPlatform = TargetPlatform.GENERIC;

  private SplittingMode splittingMode = SplittingMode.OFF;
  /** Rules that bind a interface component/componentInstance to another non interface component */
  private Set<ASTBindingRule> componentBindings = new HashSet<>();
  /** Scope of the cdLangExtension language*/
  private CDLangExtensionScope cdLangExtensionScope;

  private final PortMap componentPortMap = new PortMap();

  public PortMap getComponentPortMap() {
    return componentPortMap;
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

  /**
   * Gets the implementing component of given interface component, if the component is bound by componentBindings.
   * @param componentType interface component
   * @return implementing component if present
   */
  public Optional<ComponentTypeSymbol> getBinding(ComponentTypeSymbol componentType){
    for(ASTBindingRule binding : componentBindings){
      if(!binding.isInstance() && binding.getInterfaceComponentSymbol()==componentType){
        return Optional.of(binding.getImplementationComponentSymbol());
      }
    }
    return Optional.empty();
  }

  /**
   * Gets the implementing component of given interface component, if the component is bound by componentBindings.
   * @param componentType interface component
   * @return implementing component if present
   */
  public Optional<ASTComponentType> getBinding(ASTComponentType componentType){
    for(ASTBindingRule binding : componentBindings){
      if(!binding.isInstance()&&binding.getInterfaceComponentDefinition()==componentType){
        return Optional.of(binding.getImplementationComponentDefinition());
      }
    }
    return Optional.empty();
  }

  /**
   * Gets the implementing component of given interface component instance, if the component instance is bound by componentBindings.
   * @param componentInstance interface component instance
   * @return implementing component if present
   */
  public Optional<ComponentTypeSymbol> getBinding(ComponentInstanceSymbol componentInstance){
    for(ASTBindingRule binding : componentBindings){
      if(binding.isInstance()&&binding.getInterfaceInstanceSymbol()==componentInstance){
        return Optional.of(binding.getImplementationComponentSymbol());
      }
    }
    return Optional.empty();
  }

  /**
   * Gets the implementing component of given interface component instance, if the component instance is bound by componentBindings.
   * @param componentInstance interface component instance
   * @return implementing component if present
   */
  public Optional<ASTComponentType> getBinding(ASTComponentInstance componentInstance){
    for(ASTBindingRule binding : componentBindings){
      if(binding.isInstance()&&binding.getInterfaceInstanceDefinition()==componentInstance){
        return Optional.of(binding.getImplementationComponentDefinition());
      }
    }
    return Optional.empty();
  }

  /**
   * Checks if the given component implements any interface component or interface ComponentInstance by componentBindings.
   * @param componentType implementing component
   * @return If the component implements according to componentBindings.
   */
  public boolean isImplementation(ASTComponentType componentType){
    for(ASTBindingRule binding : componentBindings){
      if(binding.getImplementationComponentDefinition()==componentType){
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the given component implements any interface component or interface ComponentInstance by componentBindings.
   * @param componentType implementing component
   * @return If the component implements according to componentBindings.
   */
  public boolean isImplementation(ComponentTypeSymbol componentType){
    for(ASTBindingRule binding : componentBindings){
      if(binding.getImplementationComponentSymbol()==componentType){
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
