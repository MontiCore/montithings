// (c) https://github.com/MontiCore/monticore
package montithings.generator;

import arcbasis._symboltable.ComponentTypeSymbol;
import bindings.BindingsTool;
import bindings._ast.ASTBindingRule;
import bindings._ast.ASTBindingsCompilationUnit;
import bindings._cocos.BindingsCoCos;
import bindings._parser.BindingsParser;
import bindings._symboltable.BindingsGlobalScope;
import bindings._symboltable.BindingsLanguage;
import cdlangextension.CDLangExtensionTool;
import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._parser.CDLangExtensionParser;
import cdlangextension._symboltable.CDLangExtensionGlobalScope;
import cdlangextension._symboltable.CDLangExtensionLanguage;
import de.monticore.cd.CD4ACoCos;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.util.DirectoryUtil;
import montiarc.util.Modelfinder;
import montithings.MontiThingsTool;
import montithings._symboltable.IMontiThingsScope;
import montithings._symboltable.MontiThingsGlobalScope;
import montithings._symboltable.MontiThingsLanguage;
import montithings.generator.cd2cpp.CppGenerator;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.codegen.xtend.MTGenerator;
import montithings.generator.helper.ComponentHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MontiThingsGeneratorTool extends MontiThingsTool {

  private static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";
  private static final String TOOL_NAME = "MontiThingsGeneratorTool";

  public void generate(File modelPath, File target, File hwcPath, ConfigParams config) {

    /* ============================================================ */
    /* ==================== Copy HWC to target ==================== */
    /* ============================================================ */
    try {
      if (config.getTargetPlatform() == ConfigParams.TargetPlatform.ARDUINO) {
        FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath()).toFile());
      }
      else {
        FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath(), "hwc").toFile());
      }
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }

    /* ============================================================ */
    /* ======================= Check Models ======================= */
    /* ============================================================ */
    // 1. Find all .mt files
    List<String> foundModels = Modelfinder
        .getModelsInModelPath(modelPath, MontiThingsLanguage.FILE_ENDING);
    // 2. Initialize SymbolTable
    Log.info("Initializing symboltable", TOOL_NAME);
    String basedir = DirectoryUtil.getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(),
        target.getAbsolutePath());
    IMontiThingsScope symTab = initSymbolTable(modelPath,
        //Paths.get(basedir + LIBRARY_MODELS_FOLDER).toFile(),
        hwcPath);

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);

      // 3. parse + resolve model
      Log.info("Parsing model:" + qualifiedModelName, TOOL_NAME);
      ComponentTypeSymbol comp = symTab.resolveComponentType(qualifiedModelName).get();

      // 4. check cocos
      Log.info("Check model: " + qualifiedModelName, TOOL_NAME);
      checkCoCos(comp.getAstNode());
    }

    /* ============================================================ */
    /* =========================== Check CDs ====================== */
    /* ============================================================ */

    // Find all .cd files
    List<String> foundCDModels = Modelfinder.getModelFiles(CD4AnalysisLanguage.FILE_ENDING,modelPath).stream().map(e->e.toString()).collect(Collectors.toList());
    ICD4AnalysisScope symCDTab = new CD4AnalysisGlobalScopeBuilder()
        .setModelPath(new ModelPath((Paths.get(modelPath.getAbsolutePath()))))
        .setCD4AnalysisLanguage(new CD4AnalysisLanguage())
        .build();

    for (String model : foundCDModels) {
      ASTCDCompilationUnit cdAST = null;
      try {
        cdAST = new CD4AnalysisParser().parseCDCompilationUnit(model).orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: "+ model));
      }
      catch (IOException e) {
        Log.error("File '" + model + "' CD4A artifact was not found");
      }

      // parse + resolve model
      Log.info("Parsing model:" + model, "MontiThingsGeneratorTool");
      new CD4AnalysisModelLoader(new CD4AnalysisLanguage()).createSymbolTableFromAST(cdAST,model, (CD4AnalysisGlobalScope) symCDTab);

      // check cocos
      Log.info("Check model: " + model, "MontiThingsGeneratorTool");
      new CD4ACoCos().getCheckerForAllCoCos().checkAll(cdAST);
    }

    /* ============================================================ */
    /* =================== Check CDExtension ====================== */
    /* ============================================================ */

    // Find all .cd files
    List<String> foundCDExtensionModels =
      Modelfinder.getModelFiles(CDLangExtensionLanguage.FILE_ENDING,modelPath).stream().map(e->e.toString()).collect(Collectors.toList());
    CDLangExtensionTool cdExtensionTool = new CDLangExtensionTool();
    cdExtensionTool.setCdGlobalScope((CD4AnalysisGlobalScope) symCDTab);

    for (String model : foundCDExtensionModels) {
      ASTCDLangExtensionUnit cdExtensionAST = null;
      try {
        cdExtensionAST = new CDLangExtensionParser().parseCDLangExtensionUnit(model).orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: "+ model));
      }
      catch (IOException e) {
        Log.error("File '" + model + "' CDLangExtension artifact was not found");
      }

      // parse + resolve model
      Log.info("Parsing model:" + model, "MontiThingsGeneratorTool");
      if(config.getCdLangExtensionScope()==null) {
        config.setCdLangExtensionScope(cdExtensionTool.createSymboltable(cdExtensionAST, modelPath));
      }
      else{
        cdExtensionTool.createSymboltable(cdExtensionAST, (CDLangExtensionGlobalScope) config.getCdLangExtensionScope());
      }

      // check cocos
      Log.info("Check model: " + model, "MontiThingsGeneratorTool");
      new CDLangExtensionCoCos().createChecker().checkAll(cdExtensionAST);
    }

    /* ============================================================ */
    /* ====================== Check Bindings ====================== */
    /* ============================================================ */

    // 1. Get Bindings
    List<String> foundBindings = Modelfinder.getModelFiles(BindingsLanguage.FILE_ENDING,modelPath).stream().map(e->e.toString()).collect(Collectors.toList());
    Log.info("Initializing Bindings: ", "MontiArcGeneratorTool");
    BindingsTool bindingsTool = new BindingsTool();
    bindingsTool.setMtGlobalScope((MontiThingsGlobalScope) symTab);
    BindingsGlobalScope binTab = bindingsTool.initSymbolTable(modelPath);

    // 2. Parse and check Cocos of bindings
    for (String binding : foundBindings) {
      ASTBindingsCompilationUnit bindingsAST = null;
      try {
        bindingsAST = new BindingsParser().parseBindingsCompilationUnit(binding).orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: "+binding));
      }
      catch (IOException e) {
        Log.error("File '" + binding + "' Bindings artifact was not found");
      }
      Log.info("Parsing model:" + binding, TOOL_NAME);
      bindingsTool.createSymboltable(bindingsAST,binTab);

      Log.info("Check Binding: " + binding, "MontiArcGeneratorTool");
      BindingsCoCos.createChecker().checkAll(bindingsAST);

          for(bindings._ast.ASTElement rule:bindingsAST.getElementList()){
            if(rule instanceof ASTBindingRule){
              config.getComponentBindings().add((ASTBindingRule)rule);
            }
          }
    }

    /* ============================================================ */
    /* ==================== Generate Components =================== */
    /* ============================================================ */

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
      ComponentTypeSymbol comp = symTab.resolveComponentType(qualifiedModelName).get();

      // 5. generate
      Log.info("Generate model: " + qualifiedModelName, TOOL_NAME);

      // check if component is implementation
      if (config.isImplementation(comp)) {
        // Dont generate files for implementation. They are generated when interface is there
        continue;
      }

      String compname = comp.getName();

      // Check if component is interface
      Optional<ComponentTypeSymbol> implementation = config.getBinding(comp);
      if (implementation.isPresent()) {
        compname = implementation.get().getName();
      }

      // Generate Files
      MTGenerator.generateAll(
          Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName()))
              .toFile(), hwcPath, comp, foundModels, compname, config);
    }

    /* ============================================================ */
    /* ====================== CMakeLists.txt ====================== */
    /* ============================================================ */

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
      ComponentTypeSymbol comp = symTab.resolveComponentType(qualifiedModelName).get();

      if (ComponentHelper.isApplication(comp)) {
        File libraryPath = Paths.get(target.getAbsolutePath(), "montithings-RTE").toFile();
        // Check for Subpackages
        File[] subPackagesPath = getSubPackagesPath(modelPath.getAbsolutePath());

        // 6 generate make file
        if (config.getTargetPlatform() != ConfigParams.TargetPlatform.ARDUINO) { // Arduino uses its own build system
          Log.info("Generate CMake file", "MontiThingsGeneratorTool");
          MTGenerator.generateMakeFile(target, comp, hwcPath, libraryPath,
              subPackagesPath, config);
        }
      }
    }

    /* ============================================================ */
    /* ====================== Class Diagrams ====================== */
    /* ============================================================ */
    generateCD(modelPath, target);
  }

  /**
   * Returns list of all subpackages paths
   *
   * @param modelPath
   * @return
   */
  protected File[] getSubPackagesPath(String modelPath) {
    ArrayList<File> subPackagesPaths = new ArrayList<>();
    File[] subDirs = new File(modelPath).listFiles(File::isDirectory);

    // Iterate over subdirectories of the model folder and add the paths of the subdirs to array
    for (File subDir : subDirs) {
      subPackagesPaths.add(new File(subDir.getAbsolutePath()));
    }

    // cast to ArrayList to File[] and return
    return subPackagesPaths.toArray(new File[subPackagesPaths.size()]);
  }

  protected void generateCD(File modelPath, File targetFilepath) {
    List<String> foundModels = Modelfinder
        .getModelsInModelPath(modelPath, CD4AnalysisLanguage.FILE_ENDING);
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);

      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new CppGenerator(outDir, Paths.get(modelPath.getAbsolutePath()), model,
          Names.getQualifiedName(packageName, simpleName)).generate();
    }
  }

}
