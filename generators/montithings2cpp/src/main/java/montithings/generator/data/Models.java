// (c) https://github.com/MontiCore/monticore
package montithings.generator.data;

import bindings._symboltable.BindingsLanguage;
import cdlangextension._symboltable.CDLangExtensionLanguage;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisLanguage;
import montiarc.util.Modelfinder;
import montithings._symboltable.MontiThingsLanguage;

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
  private List<String> montithings = new ArrayList<>();

  /**
   * Bindings
   */
  private List<String> bindings = new ArrayList<>();

  /**
   * Class diagrams
   */
  private List<String> classdiagrams = new ArrayList<>();

  /**
   * CD Lang Extension models
   */
  private List<String> cdextensions = new ArrayList<>();

  public Models(File modelPath) {
    findModels(modelPath);
  }

  protected void findModels(File modelPath) {
    montithings = Modelfinder.getModelsInModelPath(modelPath, MontiThingsLanguage.FILE_ENDING);
    bindings = Modelfinder.getModelFiles(BindingsLanguage.FILE_ENDING, modelPath).stream()
      .map(File::toString).collect(Collectors.toList());
    classdiagrams = Modelfinder.getModelFiles(CD4AnalysisLanguage.FILE_ENDING, modelPath).stream()
      .map(File::toString).collect(Collectors.toList());
    cdextensions = Modelfinder.getModelFiles(CDLangExtensionLanguage.FILE_ENDING, modelPath).stream()
      .map(File::toString).collect(Collectors.toList());
  }

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public Models(List<String> montithings, List<String> bindings,
    List<String> classdiagrams, List<String> cdextensions) {
    this.montithings = montithings;
    this.bindings = bindings;
    this.classdiagrams = classdiagrams;
    this.cdextensions = cdextensions;
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
}
