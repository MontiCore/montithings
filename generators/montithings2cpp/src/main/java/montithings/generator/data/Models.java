// (c) https://github.com/MontiCore/monticore
package montithings.generator.data;

import bindings.BindingsTool;
import cdlangextension.CDLangExtensionTool;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import montiarc.util.Modelfinder;
import montithings.MontiThingsTool;
import mtconfig.MTConfigTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Encapsulates models known to the generator
 */
public class Models {
  /**
   * MontiThings Models
   */
  protected List<String> montithings = new ArrayList<>();

  /**
   * Bindings
   */
  protected List<String> bindings = new ArrayList<>();

  /**
   * Class diagrams
   */
  protected List<String> classdiagrams = new ArrayList<>();

  /**
   * MontiThings Configuration models
   */
  protected List<String> mtConfig = new ArrayList<>();

  /**
   * CD Lang Extension models
   */
  protected List<String> cdextensions = new ArrayList<>();

  public Models(File modelPath) {
    findModels(modelPath);
  }

  protected void findModels(File modelPath) {
    montithings = Modelfinder.getModelsInModelPath(modelPath, MontiThingsTool.MT_FILE_EXTENSION);
    bindings = Modelfinder.getModelFiles(BindingsTool.FILE_ENDING, modelPath).stream()
      .map(File::toString).collect(Collectors.toList());
    classdiagrams = Modelfinder.getModelFiles(CD4AnalysisGlobalScope.EXTENSION, modelPath).stream()
      .map(File::toString).collect(Collectors.toList());
    cdextensions = Modelfinder.getModelFiles(CDLangExtensionTool.FILE_ENDING, modelPath).stream()
      .map(File::toString).collect(Collectors.toList());
    mtConfig = Modelfinder.getModelFiles(MTConfigTool.FILE_ENDING, modelPath).stream()
        .map(File::toString).collect(Collectors.toList());
  }

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public Models(List<String> montithings, List<String> bindings,
    List<String> classdiagrams, List<String> cdextensions,List<String> mtConfig) {
    this.montithings = montithings;
    this.bindings = bindings;
    this.classdiagrams = classdiagrams;
    this.cdextensions = cdextensions;
    this.mtConfig = mtConfig;
  }

  public List<String> getMontithings() {
    return montithings;
  }

  public void setMontithings(List<String> montithings) {
    this.montithings = montithings;
  }

  public List<String> getBindings() {
    return bindings;
  }

  public void setBindings(List<String> bindings) {
    this.bindings = bindings;
  }

  public List<String> getClassdiagrams() {
    return classdiagrams;
  }

  public void setClassdiagrams(List<String> classdiagrams) {
    this.classdiagrams = classdiagrams;
  }

  public List<String> getCdextensions() {
    return cdextensions;
  }

  public void setCdextensions(List<String> cdextensions) {
    this.cdextensions = cdextensions;
  }

  public List<String> getMTConfig() {
    return mtConfig;
  }

  public void setMTConfig(List<String> mtConfig) {
    this.mtConfig = mtConfig;
  }
}
