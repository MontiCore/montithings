// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import bindings._ast.ASTBindingRule;
import cdlangextension._symboltable.CDLangExtensionScope;
import de.monticore.utils.Names;
import montithings.generator.data.PortMap;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

  /** property for message brokers */

  private MessageBroker messageBroker = MessageBroker.OFF;

  public MessageBroker getMessageBroker() {
    return messageBroker;
  }

  public void setMessageBroker(MessageBroker messageBroker) {
    this.messageBroker = messageBroker;
  }

  public enum MessageBroker {
    OFF("OFF"),
    MQTT("MQTT");

    String name;

    MessageBroker(String name) {
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

  /** Absolute path to the directory that contains handwritten templates in subdirectories according to their package.*/
  public Path hwcTemplatePath;

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

  /**
   * Gets the qualified name of the handwritten port implementation if it is present.
   * @param port The port for which to check for a handwritten implementation.
   * @return The qualified type name of the port that is defined by given templates for the given port.
   * If no fitting templates are present Optional.empty is returned.
   */
  public Optional<String> getAdditionalPort(PortSymbol port){
    String packageName = Names.getQualifier(Names.getQualifier(port.getFullName()));
    String componentName = StringUtils.capitalize(Names.getSimpleName(Names.getQualifier(port.getFullName())));
    File exists = new File(hwcTemplatePath +File.separator+ Names.getPathFromPackage(packageName)+File.separator+componentName+ StringUtils.capitalize(port.getName())+"PortBody.ftl");
    if(exists.exists()&&exists.isFile()){
      return Optional.of(packageName+"."+componentName+StringUtils.capitalize(port.getName())+"Port");
    }
    else{
      return Optional.empty();
    }
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
