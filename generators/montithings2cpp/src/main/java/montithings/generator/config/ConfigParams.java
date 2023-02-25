// (c) https://github.com/MontiCore/monticore
package montithings.generator.config;

import arcbasis._ast.ASTComponentInstance;
import arcbasis._ast.ASTComponentType;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import bindings._ast.ASTBindingRule;
import cdlangextension._symboltable.ICDLangExtensionScope;
import com.google.common.collect.Multimap;
import montithings.generator.data.PortMap;
import mtconfig._symboltable.IMTConfigGlobalScope;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Bundle of parameters for montithings2cpp generator.
 *
 * @since 5.0.2
 */
public class ConfigParams {

  /**
   * property for message brokers
   */
  protected MessageBroker messageBroker = MessageBroker.OFF;

  /**
   * property for log tracing
   */
  protected LogTracing logTracing = LogTracing.OFF;

  /**
   * property for replay mode
   */
  protected ReplayMode replayMode = ReplayMode.OFF;

  protected ApplyPatterns applyAnomalyDetectionPattern = ApplyPatterns.OFF;

  protected ApplyPatterns applyNetworkMinimizationPattern = ApplyPatterns.OFF;;

  protected ApplyPatterns applyGrafanaPattern = ApplyPatterns.OFF;

  protected RecordingMode recordingMode = RecordingMode.OFF;

  protected PortNameTrafo portNameTrafo = PortNameTrafo.OFF;

  protected String grafanaInstanceUrl = "";

  protected String grafanaApiKey = "";

  /**
   * property for target platform
   */
  protected TargetPlatform targetPlatform = TargetPlatform.GENERIC;

  protected SplittingMode splittingMode = SplittingMode.OFF;

  protected SerializationMode serializationMode = SerializationMode.JSON;

  protected String projectVersion;

  /**
   * Rules that bind a interface component/componentInstance to another non interface component
   */
  protected Set<ASTBindingRule> componentBindings = new HashSet<>();

  /**
   * Unconnected ports that have hand-written templates available.
   */
  protected Set<PortSymbol> templatedPorts = new HashSet<>();

  /**
   * Scope of the cdLangExtension language
   */
  protected ICDLangExtensionScope cdLangExtensionScope;

  /**
   * Scope of the MTConfig language
   */
  protected IMTConfigGlobalScope mtConfigScope;

  /**
   * Maps MontiThings components to network ports for local web socket communication
   */
  protected final PortMap componentPortMap = new PortMap();

  /**
   * Directory that contains handwritten code for components.
   */
  protected File hwcPath;

  /**
   * All type arguments which which a component type is ever instantiated
   */
  Multimap<ComponentTypeSymbol, String> typeArguments;

  /**
   * Absolute path to the directory that contains handwritten templates in subdirectories according to their package.
   */
  protected Path hwcTemplatePath;

  /**
   * fully qualified name of the component that acts as the main (outermost) component
   */
  protected String mainComponent;

  /**
   * path of file containing recordings
   */
  protected File replayDataFile;

  /**
   * Gets the implementing component of given interface component, if the component is bound by componentBindings.
   *
   * @param componentType interface component
   * @return implementing component if present
   */
  public Optional<ComponentTypeSymbol> getBinding(ComponentTypeSymbol componentType) {
    for (ASTBindingRule binding : componentBindings) {
      if (!binding.isInstance() && binding.getInterfaceComponentSymbol() == componentType) {
        return Optional.of(binding.getImplementationComponentSymbol());
      }
    }
    return Optional.empty();
  }

  /**
   * Gets the implementing component of given interface component, if the component is bound by componentBindings.
   *
   * @param componentType interface component
   * @return implementing component if present
   */
  public Optional<ASTComponentType> getBinding(ASTComponentType componentType) {
    for (ASTBindingRule binding : componentBindings) {
      if (!binding.isInstance() && binding.getInterfaceComponentSymbol().getAstNode() == componentType) {
        return Optional.of(binding.getInterfaceComponentSymbol().getAstNode());
      }
    }
    return Optional.empty();
  }

  /**
   * Gets the implementing component of given interface component instance, if the component instance is bound by componentBindings.
   *
   * @param componentInstance interface component instance
   * @return implementing component if present
   */
  public Optional<ComponentTypeSymbol> getBinding(ComponentInstanceSymbol componentInstance) {
    for (ASTBindingRule binding : componentBindings) {
      if (binding.isInstance() && binding.getInterfaceInstanceSymbol() == componentInstance) {
        return Optional.of(binding.getImplementationComponentSymbol());
      }
    }
    return Optional.empty();
  }

  /**
   * Gets the implementing component of given interface component instance, if the component instance is bound by componentBindings.
   *
   * @param componentInstance interface component instance
   * @return implementing component if present
   */
  public Optional<ASTComponentType> getBinding(ASTComponentInstance componentInstance) {
    for (ASTBindingRule binding : componentBindings) {
      if (binding.isInstance() && binding.getInterfaceInstanceSymbol().getAstNode() == componentInstance) {
        return Optional.of(binding.getImplementationComponentSymbol().getAstNode());
      }
    }
    return Optional.empty();
  }

  /**
   * Checks if the given component implements any interface component or interface ComponentInstance by componentBindings.
   *
   * @param componentType implementing component
   * @return If the component implements according to componentBindings.
   */
  public boolean isImplementation(ASTComponentType componentType) {
    for (ASTBindingRule binding : componentBindings) {
      if (binding.getImplementationComponentSymbol().getAstNode() == componentType) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the given component implements any interface component or interface ComponentInstance by componentBindings.
   *
   * @param componentType implementing component
   * @return If the component implements according to componentBindings.
   */
  public boolean isImplementation(ComponentTypeSymbol componentType) {
    for (ASTBindingRule binding : componentBindings) {
      if (binding.getImplementationComponentSymbol() == componentType) {
        return true;
      }
    }
    return false;
  }

  /**
   * Wrapper for typeArguments.get(). Only necessary to avoid Freemarker problems
   */
  public Collection<String> getTypeArguments(ComponentTypeSymbol componentType) {
    return typeArguments.get(componentType);
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public MessageBroker getMessageBroker() {
    return messageBroker;
  }

  public void setMessageBroker(MessageBroker messageBroker) {
    this.messageBroker = messageBroker;
  }

  public ReplayMode getReplayMode() {
    return replayMode;
  }

  public void setReplayMode(ReplayMode replayMode) {
    this.replayMode = replayMode;
  }

  public ApplyPatterns getApplyAnomalyDetectionPattern() {
    return applyAnomalyDetectionPattern;
  }

  public void setApplyAnomalyDetectionPattern(ApplyPatterns applyPatterns) {
    this.applyAnomalyDetectionPattern = applyPatterns;
  }

  public ApplyPatterns getApplyNetworkMinimizationPattern() {
    return applyNetworkMinimizationPattern;
  }

  public void setApplyNetworkMinimizationPattern(ApplyPatterns applyPatterns) {
    this.applyNetworkMinimizationPattern = applyPatterns;
  }

  public ApplyPatterns getApplyGrafanaPattern() {
    return applyGrafanaPattern;
  }

  public void setApplyGrafanaPattern(ApplyPatterns applyPatterns) {
    this.applyGrafanaPattern = applyPatterns;
  }

  public String getGrafanaApiKey() {
    return grafanaApiKey;
  }

  public void setGrafanaApiKey(String grafanaApiKey) {
    this.grafanaApiKey = grafanaApiKey;
  }

  public String getGrafanaInstanceUrl() {
    return grafanaInstanceUrl;
  }

  public void setGrafanaInstanceUrl(String grafanaInstanceUrl) {
    this.grafanaInstanceUrl = grafanaInstanceUrl;
  }

  public File getHwcPath() {
    return hwcPath;
  }

  public void setHwcPath(File hwcPath) {
    this.hwcPath = hwcPath;
  }

  public Path getHwcTemplatePath() {
    return hwcTemplatePath;
  }

  public void setHwcTemplatePath(Path hwcTemplatePath) {
    this.hwcTemplatePath = hwcTemplatePath;
  }

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

  public Set<PortSymbol> getTemplatedPorts() {
    return templatedPorts;
  }

  public void setTemplatedPorts(Set<PortSymbol> templatedPorts) {
    this.templatedPorts = templatedPorts;
  }

  public ICDLangExtensionScope getCdLangExtensionScope() {
    return cdLangExtensionScope;
  }

  public void setCdLangExtensionScope(ICDLangExtensionScope cdLangExtensionScope) {
    this.cdLangExtensionScope = cdLangExtensionScope;
  }

  public IMTConfigGlobalScope getMtConfigScope() {
    return mtConfigScope;
  }

  public void setMtConfigScope(IMTConfigGlobalScope mtConfigScope) {
    this.mtConfigScope = mtConfigScope;
  }

  public SplittingMode getSplittingMode() {
    return splittingMode;
  }

  public void setSplittingMode(SplittingMode splittingMode) {
    this.splittingMode = splittingMode;
  }

  public SerializationMode getSerializationMode() {
    return serializationMode;
  }

  public void setSerializationMode(SerializationMode serializationMode) {
    this.serializationMode = serializationMode;
  }

  public LogTracing getLogTracing() {
    return logTracing;
  }

  public void setLogTracing(LogTracing logTracing) {
    this.logTracing = logTracing;
  }

  public RecordingMode getRecordingMode() {
    return recordingMode;
  }

  public void setRecordingMode(RecordingMode recordingMode) {
    this.recordingMode = recordingMode;
  }

  public PortNameTrafo getPortNameTrafo() {
    return portNameTrafo;
  }

  public void setPortNameTrafo(PortNameTrafo portNameTrafo) {
    this.portNameTrafo = portNameTrafo;
  }

  public Multimap<ComponentTypeSymbol, String> getTypeArguments() {
    return typeArguments;
  }

  public void setTypeArguments(
      Multimap<ComponentTypeSymbol, String> typeArguments) {
    this.typeArguments = typeArguments;
  }

  public String getProjectVersion() {
    return projectVersion;
  }

  public void setProjectVersion(String projectVersion) {
    this.projectVersion = projectVersion;
  }

  public String getMainComponent() {
    return mainComponent;
  }

  public void setMainComponent(String mainComponent) {
    this.mainComponent = mainComponent;
  }

  public File getReplayDataFile() {
    return replayDataFile;
  }

  public void setReplayDataFile(File replayDataFile) {
    this.replayDataFile = replayDataFile;
  }
}
