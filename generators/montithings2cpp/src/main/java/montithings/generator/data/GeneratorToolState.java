// (c) https://github.com/MontiCore/monticore
package montithings.generator.data;

import arcbasis._symboltable.ComponentTypeSymbol;
import bindings.BindingsTool;
import bindings._symboltable.IBindingsGlobalScope;
import cdlangextension.CDLangExtensionTool;
import de.monticore.io.paths.ModelPath;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.codegen.MTGenerator;
import montithings.generator.config.ConfigParams;
import mtconfig.MTConfigTool;
import mtconfig._symboltable.IMTConfigGlobalScope;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeneratorToolState {

  /**
   * The tool itself (enables steps to use tool's methods
   */
  MontiThingsGeneratorTool tool;

  /**
   * The generator itself
   */
  protected MTGenerator mtg;

  /**
   * Directory path to models (.mt, .mt files,
   */
  protected File modelPath;

  /**
   * Model path in MontiCore's format
   */
  protected ModelPath mcModelPath;

  /**
   * Path where to place generated sources code
   */
  protected File target;

  /**
   * Directory path where to find handwritten code
   */
  protected File hwcPath;

  /**
   * Directory path where to find test code
   */
  protected File testPath;

  /**
   * Configuration parameters set by the user (e.g. splitting mode or message broker)
   */
  protected ConfigParams config;

  /**
   * Collection of models used for generating code
   */
  protected Models models;

  /**
   * Directory in which .sym files are stores
   */
  protected String symbolPath;

  /**
   * Scope of different languages
   */
  protected IMontiThingsGlobalScope symTab;

  protected IBindingsGlobalScope binTab;

  protected IMTConfigGlobalScope mtConfigGlobalScope;

  protected CDLangExtensionTool cdExtensionTool;

  protected BindingsTool bindingsTool;

  protected MTConfigTool mtConfigTool;

  protected HashSet<ComponentTypeSymbol> executableComponents;

  protected List<String> executableSubdirs;

  protected List<String> executableSensorActuatorPorts;

  protected Map<ComponentTypeSymbol, Set<ComponentTypeSymbol>> modelPacks;

  protected List<String> hwcPythonScripts;

  protected List<Pair<ComponentTypeSymbol, String>> instances;

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public GeneratorToolState(MontiThingsGeneratorTool tool, File modelPath, File target,
    File hwcPath, File testPath, ConfigParams config) {
    this.tool = tool;
    this.modelPath = modelPath;
    this.target = target;
    this.hwcPath = hwcPath;
    this.testPath = testPath;
    this.config = config;
  }

  public MontiThingsGeneratorTool getTool() {
    return tool;
  }

  public void setTool(MontiThingsGeneratorTool tool) {
    this.tool = tool;
  }

  public MTGenerator getMtg() {
    return mtg;
  }

  public void setMtg(MTGenerator mtg) {
    this.mtg = mtg;
  }

  public File getModelPath() {
    return modelPath;
  }

  public void setModelPath(File modelPath) {
    this.modelPath = modelPath;
  }

  public ModelPath getMcModelPath() {
    return mcModelPath;
  }

  public void setMcModelPath(ModelPath mcModelPath) {
    this.mcModelPath = mcModelPath;
  }

  public File getTarget() {
    return target;
  }

  public void setTarget(File target) {
    this.target = target;
  }

  public File getHwcPath() {
    return hwcPath;
  }

  public void setHwcPath(File hwcPath) {
    this.hwcPath = hwcPath;
  }

  public File getTestPath() {
    return testPath;
  }

  public void setTestPath(File testPath) {
    this.testPath = testPath;
  }

  public ConfigParams getConfig() {
    return config;
  }

  public void setConfig(ConfigParams config) {
    this.config = config;
  }

  public Models getModels() {
    return models;
  }

  public void setModels(Models models) {
    this.models = models;
  }

  public String getSymbolPath() {
    return symbolPath;
  }

  public void setSymbolPath(String symbolPath) {
    this.symbolPath = symbolPath;
  }

  public IMontiThingsGlobalScope getSymTab() {
    return symTab;
  }

  public void setSymTab(IMontiThingsGlobalScope symTab) {
    this.symTab = symTab;
  }

  public IBindingsGlobalScope getBinTab() {
    return binTab;
  }

  public void setBinTab(IBindingsGlobalScope binTab) {
    this.binTab = binTab;
  }

  public IMTConfigGlobalScope getMtConfigGlobalScope() {
    return mtConfigGlobalScope;
  }

  public void setMtConfigGlobalScope(IMTConfigGlobalScope mtConfigGlobalScope) {
    this.mtConfigGlobalScope = mtConfigGlobalScope;
  }

  public CDLangExtensionTool getCdExtensionTool() {
    return cdExtensionTool;
  }

  public void setCdExtensionTool(CDLangExtensionTool cdExtensionTool) {
    this.cdExtensionTool = cdExtensionTool;
  }

  public BindingsTool getBindingsTool() {
    return bindingsTool;
  }

  public void setBindingsTool(BindingsTool bindingsTool) {
    this.bindingsTool = bindingsTool;
  }

  public MTConfigTool getMtConfigTool() {
    return mtConfigTool;
  }

  public void setMtConfigTool(MTConfigTool mtConfigTool) {
    this.mtConfigTool = mtConfigTool;
  }

  public HashSet<ComponentTypeSymbol> getExecutableComponents() {
    return executableComponents;
  }

  public void setExecutableComponents(
    HashSet<ComponentTypeSymbol> executableComponents) {
    this.executableComponents = executableComponents;
  }

  public List<String> getExecutableSubdirs() {
    return executableSubdirs;
  }

  public void setExecutableSubdirs(List<String> executableSubdirs) {
    this.executableSubdirs = executableSubdirs;
  }

  public List<String> getExecutableSensorActuatorPorts() {
    return executableSensorActuatorPorts;
  }

  public void setExecutableSensorActuatorPorts(
    List<String> executableSensorActuatorPorts) {
    this.executableSensorActuatorPorts = executableSensorActuatorPorts;
  }

  public Map<ComponentTypeSymbol, Set<ComponentTypeSymbol>> getModelPacks() {
    return modelPacks;
  }

  public void setModelPacks(
    Map<ComponentTypeSymbol, Set<ComponentTypeSymbol>> modelPacks) {
    this.modelPacks = modelPacks;
  }

  public List<String> getHwcPythonScripts() {
    return hwcPythonScripts;
  }

  public void setHwcPythonScripts(List<String> hwcPythonScripts) {
    this.hwcPythonScripts = hwcPythonScripts;
  }

  public List<Pair<ComponentTypeSymbol, String>> getInstances() {
    return instances;
  }

  public void setInstances(
    List<Pair<ComponentTypeSymbol, String>> instances) {
    this.instances = instances;
  }

}
