// (c) https://github.com/MontiCore/monticore
package montithings.generator;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import bindings.BindingsTool;
import bindings._ast.ASTBindingRule;
import bindings._ast.ASTBindingsCompilationUnit;
import bindings._cocos.BindingsCoCos;
import bindings._parser.BindingsParser;
import bindings._symboltable.IBindingsGlobalScope;
import cdlangextension.CDLangExtensionTool;
import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._cocos.CDLangExtensionCoCos;
import cdlangextension._parser.CDLangExtensionParser;
import cdlangextension._symboltable.CDLangExtensionUnitSymbol;
import cdlangextension._symboltable.ICDLangExtensionGlobalScope;
import cdlangextension._symboltable.ICDLangExtensionScope;
import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montiarc.util.Modelfinder;
import montithings.MontiThingsMill;
import montithings.MontiThingsTool;
import montithings._ast.ASTMTComponentType;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.IMontiThingsScope;
import montithings.cocos.PortConnection;
import montithings.generator.cd2cpp.CppGenerator;
import montithings.generator.cocos.ComponentHasBehavior;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.codegen.MTGenerator;
import montithings.generator.codegen.ConfigParams.MessageBroker;
import montithings.generator.codegen.ConfigParams.SplittingMode;
import montithings.generator.data.Models;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.GeneratorHelper;
import montithings.generator.visitor.FindTemplatedPortsVisitor;
import montithings.generator.visitor.GenericInstantiationVisitor;
import montithings.util.MontiThingsError;
import mtconfig.MTConfigTool;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.MTConfigCoCos;
import mtconfig._parser.MTConfigParser;
import mtconfig._symboltable.IMTConfigGlobalScope;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import static montithings.generator.helper.FileHelper.*;

public class MontiThingsGeneratorTool extends MontiThingsTool {

  protected static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";

  protected static final String TOOL_NAME = "MontiThingsGeneratorTool";

  protected MTGenerator mtg;

  public void generate(File modelPath, File target, File hwcPath, File testPath,
    ConfigParams config) {

    //Log.initWARN();
    Log.enableFailQuick(false);

    ModelPath mp = new ModelPath(modelPath.toPath());
    mtg = new MTGenerator(target, hwcPath, config);

    /* ============================================================ */
    /* ==================== Copy HWC to target ==================== */
    /* ============================================================ */
    copyHwcToTarget(target, hwcPath, config);

    /* ============================================================ */
    /* ======================== Find Models ======================= */
    /* ============================================================ */
    Models models = new Models(modelPath);

    /* ============================================================ */
    /* ===================== Set up Symbol Tabs =================== */
    /* ============================================================ */
    Log.info("Initializing symboltable", TOOL_NAME);

    ICD4CodeGlobalScope cd4CGlobalScope = CD4CodeMill.cD4CodeGlobalScopeBuilder()
      .setModelPath(mp)
      .setModelFileExtension(CD_FILE_EXTENSION)
      .build();
    IMontiThingsGlobalScope symTab = MontiThingsMill.montiThingsGlobalScopeBuilder()
      .setModelPath(mp)
      .setModelFileExtension(MT_FILE_EXTENSION)
      .build();
    resolvingDelegates(symTab, cd4CGlobalScope);
    addBasicTypes(symTab);

    CDLangExtensionTool cdExtensionTool = new CDLangExtensionTool();
    cdExtensionTool.setCdGlobalScope(cd4CGlobalScope);

    BindingsTool bindingsTool = new BindingsTool();
    bindingsTool.setMtGlobalScope(symTab);
    IBindingsGlobalScope binTab = bindingsTool.initSymbolTable(modelPath);

    MTConfigTool mtConfigTool = new MTConfigTool();
    mtConfigTool.setMtGlobalScope(symTab);

    /* ============================================================ */
    /* ====================== Check Models ======================== */
    /* ============================================================ */
    Log.info("Checking models", TOOL_NAME);

    processModels(cd4CGlobalScope, true);
    processModels(symTab, false);
    checkIfMainComponentExists(symTab, models, config);
    checkCdExtensionModels(models.getCdextensions(), modelPath, config, cdExtensionTool);
    checkBindings(models.getBindings(), config, bindingsTool, binTab);
    checkMTConfig(models.getMTConfig(), config, mtConfigTool,
      mtConfigTool.initSymbolTable(modelPath));

    /* ============================================================ */
    /* =================== Find Code Templates ==================== */
    /* ============================================================ */

    Log.info("Looking for code templates", TOOL_NAME);

    GenericInstantiationVisitor genericInstantiationVisitor = new GenericInstantiationVisitor();

    for (String model : models.getMontithings()) {
      // Parse model
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
      ComponentTypeSymbol comp = symTab.resolveComponentType(qualifiedModelName).get();

      Log.info("Searching templates for: " + comp.getFullName(), TOOL_NAME);

      // Find ports with templates
      FindTemplatedPortsVisitor vistor = new FindTemplatedPortsVisitor(config);
      comp.getAstNode().accept(vistor);
      config.getTemplatedPorts().addAll(vistor.getTemplatedPorts());

      comp.getAstNode().accept(genericInstantiationVisitor);
    }

    config.setTypeArguments(genericInstantiationVisitor.getTypeArguments());

    checkMtGeneratorCoCos(symTab, config);

    /* ============================================================ */
    /* ====================== Generate Code ======================= */
    /* ============================================================ */
    
    // Collect all the instances of the executable components (Some components
    // may only be included in other components and thus do not need an own
    // executable).
    ComponentTypeSymbol mainCompSymbol = modelToSymbol(config.getMainComponent(), symTab);
    List<Pair<ComponentTypeSymbol, String>> instances = ComponentHelper.getExecutableInstances(mainCompSymbol, config);
    HashSet<ComponentTypeSymbol> executableComponents = new HashSet<>();
    for(Pair<ComponentTypeSymbol, String> instance : instances) {
      executableComponents.add(instance.getKey());
    }
    
    // Aggregate all the target folders for the components.
    List<String> executableSubdirs = new ArrayList<>(instances.size());
    for(ComponentTypeSymbol comp : executableComponents) {
      executableSubdirs.add(comp.getFullName());
    }
    
    // determine the packs of components for each (base) model
    Map<ComponentTypeSymbol, Set<ComponentTypeSymbol>> modelPacks = new HashMap<>();
    for (String model : models.getMontithings()) {
      ComponentTypeSymbol comp = modelToSymbol(model, symTab);
      
      // If this component does not need its own executable, then we can just
      // ignore it right here. If splitting is turned of, we will generate
      // everything due to compatibility reasons.
      if (executableComponents.contains(comp) || config.getSplittingMode() == SplittingMode.OFF) {
        // aggregate all of the components that should be packed with this
        // component
        Set<ComponentTypeSymbol> includeModels = new HashSet<>();
        modelPacks.put(comp, includeModels);
        
        // the component itself should obviously be part of the deployment
        includeModels.add(comp);
        
        // all (in-)direct sub-components should be part of the deployment if
        // component should be deployed with its subcomponents
        if (ComponentHelper.shouldIncludeSubcomponents(comp, config)) {
          for (ComponentTypeSymbol sub : ComponentHelper.getSubcompTypesRecursive(comp)) {
            Log.debug("Including model \"" + sub.getFullName() + "\" with deployment of \"" + comp.getFullName() + "\"", TOOL_NAME);
            includeModels.add(sub);
          }
        }
      }
    }
    
    if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
      mtg.generateMakeFileForSubdirs(target, executableSubdirs, config);
    }

    for (Entry<ComponentTypeSymbol, Set<ComponentTypeSymbol>> e : modelPacks.entrySet()) {
      String baseModel = e.getKey().getFullName();
      Set<ComponentTypeSymbol> enclosingModels = e.getValue();
      

      File compTarget = target;

      if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
        compTarget = Paths.get(target.getAbsolutePath(), baseModel).toFile();
        mtg = new MTGenerator(compTarget, hwcPath, config);

        if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
          ComponentTypeSymbol comp = modelToSymbol(baseModel, symTab);
          mtg.generatePortJson(compTarget, comp);
        }

        generateCDEAdapter(compTarget, config);
      }
      if (config.getMessageBroker() == ConfigParams.MessageBroker.DDS) {
        mtg.generateDDSDCPSConfig(compTarget);
      }

      // Save splitting mode and message broker for overriding it for subcomponents that should be included in the same binary.
      SplittingMode orgSplit = config.getSplittingMode();
      MessageBroker orgBroker = config.getMessageBroker();
      
      for (ComponentTypeSymbol symModel : enclosingModels) {
        String model = symModel.getFullName();
        boolean genDeploy = model.equals(baseModel);
        
        // Only the deployed component should communicate directly with the 'outer world'.
        // All the other enclosed components should communicate using native ports. 
        config.setSplittingMode(genDeploy ? orgSplit : SplittingMode.OFF);
        config.setMessageBroker(genDeploy ? orgBroker : MessageBroker.OFF);
        
        generateCppForComponent(model, symTab, compTarget, hwcPath, config, genDeploy);
      }
      // reset splitting mode and message broker
      config.setSplittingMode(orgSplit);
      config.setMessageBroker(orgBroker);
      
      generateCMakeForComponent(baseModel, symTab, modelPath, compTarget, hwcPath, config, models, executableSubdirs);
      
      mtg = new MTGenerator(target, hwcPath, config);
    }

    if (config.getSplittingMode() == ConfigParams.SplittingMode.OFF) {
      generateCDEAdapter(target, config);
    }
    generateCD(modelPath, target);
    mtg.generateBuildScript(target);

    for (String model : models.getMontithings()) {
      ComponentTypeSymbol comp = modelToSymbol(model, symTab);
      if (ComponentHelper.isApplication(comp, config)) {
        mtg.generateDockerfileScript(target, comp);
      }
    }

    if (testPath != null && !testPath.toString().equals("")) {
      if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
        Log.info("--------------------------------", "MontiThingsGeneratorTool");
        Log.info("Generate Test Sources", "MontiThingsGeneratorTool");
        Log.info("--------------------------------", "MontiThingsGeneratorTool");
        config.setSplittingMode(ConfigParams.SplittingMode.OFF);
        MontiThingsGeneratorTool testTool = new MontiThingsGeneratorTool();
        testTool.generate(modelPath, Paths
          .get(Paths.get(target.getAbsolutePath()).getParent().toString(), "generated-test-sources")
          .toFile(), hwcPath, testPath, config);
      }
      else {
        for (String model : models.getMontithings()) {
          ComponentTypeSymbol comp = modelToSymbol(model, symTab);
          if (ComponentHelper.isApplication(comp, config)) {
            generateTests(modelPath, testPath, target, hwcPath, comp, config);
          }
        }
      }
    }
  }


  /* ============================================================ */
  /* ====================== Check Models ======================== */
  /* ============================================================ */

  protected void checkIfMainComponentExists(IMontiThingsGlobalScope symTab,
    Models models, ConfigParams config) {
    List<String> allComponentsList = models.getMontithings().stream()
      .map(c -> Names.getQualifier(c) + "." + Names.getSimpleName(c))
      .collect(Collectors.toList());

    String allComponents = String.join(", ", allComponentsList);

    if (!symTab.resolveComponentType(config.getMainComponent()).isPresent()) {
      Log.error(String.format(MontiThingsError.GENERATOR_MAIN_UNKNOWN.toString(),
        config.getMainComponent(), allComponents)
      );
    }
  }

  protected void checkMtGeneratorCoCos(IMontiThingsGlobalScope symTab, ConfigParams config) {
    mtChecker.addCoCo(new ComponentHasBehavior(config.getHwcPath()));
    mtChecker.addCoCo(new PortConnection(config.getTemplatedPorts()));
    for (IMontiThingsScope as : symTab.getSubScopes()) {
      ASTMACompilationUnit a = (ASTMACompilationUnit) as.getAstNode();
      Log.info("Check model: " + a.getComponentType().getSymbol().getFullName(), TOOL_NAME);
      a.accept(this.getMTChecker());
    }
  }

  protected void checkCdExtensionModels(List<String> foundCDExtensionModels, File modelPath,
    ConfigParams config, CDLangExtensionTool cdExtensionTool) {
    for (String model : foundCDExtensionModels) {
      ASTCDLangExtensionUnit cdExtensionAST = null;
      try {
        cdExtensionAST = new CDLangExtensionParser().parseCDLangExtensionUnit(model)
          .orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: " + model));
      }
      catch (IOException e) {
        Log.error("File '" + model + "' CDLangExtension artifact was not found");
      }

      // parse + resolve model
      Log.info("Parsing model: " + model, "MontiThingsGeneratorTool");
      if (config.getCdLangExtensionScope() == null) {
        config
          .setCdLangExtensionScope(cdExtensionTool.createSymboltable(cdExtensionAST, modelPath));
      }
      else {
        cdExtensionTool.createSymboltable(cdExtensionAST,
          (ICDLangExtensionGlobalScope) config.getCdLangExtensionScope());
      }

      // check cocos
      Log.info("Check model: " + model, "MontiThingsGeneratorTool");
      new CDLangExtensionCoCos().createChecker().checkAll(cdExtensionAST);
    }
  }

  protected void checkBindings(List<String> foundBindings, ConfigParams config,
    BindingsTool bindingsTool, IBindingsGlobalScope binTab) {
    for (String binding : foundBindings) {
      ASTBindingsCompilationUnit bindingsAST = null;
      try {
        bindingsAST = new BindingsParser().parseBindingsCompilationUnit(binding)
          .orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: " + binding));
      }
      catch (IOException e) {
        Log.error("File '" + binding + "' Bindings artifact was not found");
      }
      Log.info("Parsing model: " + binding, TOOL_NAME);
      bindingsTool.createSymboltable(bindingsAST, binTab);

      Log.info("Check Binding: " + binding, "MontiArcGeneratorTool");
      BindingsCoCos.createChecker().checkAll(bindingsAST);

      for (bindings._ast.ASTElement rule : bindingsAST.getElementList()) {
        if (rule instanceof ASTBindingRule) {
          config.getComponentBindings().add((ASTBindingRule) rule);
        }
      }
    }
  }

  protected void checkMTConfig(List<String> foundModels, ConfigParams config,
    MTConfigTool mtConfigTool, IMTConfigGlobalScope symTab) {
    for (String model : foundModels) {
      ASTMTConfigUnit ast = null;
      try {
        ast = new MTConfigParser().parseMTConfigUnit(model)
          .orElseThrow(() -> new NullPointerException("0xMT1111 Failed to parse: " + model));
      }
      catch (IOException e) {
        Log.error("File '" + model + "' MTConfig artifact was not found");
      }

      // parse + resolve model
      Log.info("Parsing model: " + model, "MontiThingsGeneratorTool");
      if (config.getMtConfigScope() == null) {
        config.setMtConfigScope(mtConfigTool.createSymboltable(ast, symTab));
      }

      // check cocos
      Log.info("Check model: " + model, "MontiThingsGeneratorTool");
      MTConfigCoCos.createChecker().checkAll(ast);
    }
  }

  /* ============================================================ */
  /* ===================== Generate Code ======================== */
  /* ============================================================ */

  protected void generateCppForComponent(String model, IMontiThingsScope symTab, File target,
    File hwcPath, ConfigParams config) {
    generateCppForComponent(model, symTab, target, hwcPath, config, true);
  }

  protected void generateCppForComponent(String model, IMontiThingsScope symTab, File target,
    File hwcPath, ConfigParams config, boolean generateDeploy) {
    ComponentTypeSymbol comp = modelToSymbol(model, symTab);
    Log.info("Generate MT model: " + comp.getFullName(), TOOL_NAME);

    // check if component is implementation
    if (comp.getAstNode() instanceof ASTMTComponentType &&
      ((ASTMTComponentType) comp.getAstNode()).getMTComponentModifier().isInterface()) {
      // Dont generate files for implementation. They are generated when interface is there
      return;
    }

    String compname = comp.getName();

    // Check if component is interface
    Optional<ComponentTypeSymbol> implementation = config.getBinding(comp);
    if (implementation.isPresent()) {
      compname = implementation.get().getName();
    }

    // Generate Files
    mtg.generateAll(Paths.get(target.getAbsolutePath(),
      Names.getPathFromPackage(comp.getPackageName())).toFile(),
      comp, generateDeploy);

    generateHwcPort(target, config, comp);

    if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
      copyHwcToTarget(target, hwcPath, model, config);
    }
  }

  protected void generateCppForSubcomponents(String model, File modelPath, List<String> mtModels,
    IMontiThingsScope symTab, File target, File hwcPath, ConfigParams config) {
    // Find subcomponent types
    ComponentTypeSymbol comp = modelToSymbol(model, symTab);
    List<ComponentTypeSymbol> subcomponentTypes = comp.getSubComponents().stream()
      .map(ComponentInstanceSymbol::getType).collect(Collectors.toList());

    // Generate code for each subcomponent type
    for (ComponentTypeSymbol subcomp : subcomponentTypes) {
      generateCppForComponent(subcomp.getFullName(), symTab, target, hwcPath, config, false);
      generateCppForSubcomponents(subcomp.getFullName(), modelPath, mtModels, symTab, target,
        hwcPath, config);
    }
  }

  protected void generateCMakeForComponent(String model, IMontiThingsScope symTab, File modelPath,
    File target, File hwcPath, ConfigParams config, Models models, List<String> executableInstanceNames) {
    ComponentTypeSymbol comp = modelToSymbol(model, symTab);

    if (ComponentHelper.isApplication(comp, config)
      || config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
      File libraryPath = Paths.get(target.getAbsolutePath(), "montithings-RTE").toFile();
      // Check for Subpackages
      File[] subPackagesPath = getSubPackagesPath(modelPath.getAbsolutePath());

      // 6 generate make file
      if (config.getTargetPlatform()
        != ConfigParams.TargetPlatform.ARDUINO) { // Arduino uses its own build system
        Log.info("Generate CMake file for " + comp.getFullName(), "MontiThingsGeneratorTool");
        mtg.generateMakeFile(target, comp, libraryPath, subPackagesPath);
        if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
          mtg.generateScripts(target, comp, executableInstanceNames);
        }
      }
    }
  }

  protected void generateCD(File modelPath, File targetFilepath) {
    List<String> foundModels = Modelfinder
      .getModelsInModelPath(modelPath, CD4AnalysisGlobalScope.EXTENSION);
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);

      Log.info("Generate CD model: " + model, TOOL_NAME);
      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new CppGenerator(outDir, Paths.get(modelPath.getAbsolutePath()), model)
        //.generate(Optional.of(Names.getQualifiedName(packageName, simpleName)));
        //.generate(Optional.of(packageName));
        .generate(Optional.empty());
    }
  }

  protected void generateCDEAdapter(File targetFilepath, ConfigParams config) {
    if (config.getCdLangExtensionScope() != null) {
      for (ICDLangExtensionScope subScope : config.getCdLangExtensionScope().getSubScopes()) {
        for (CDLangExtensionUnitSymbol unit : subScope.getCDLangExtensionUnitSymbols().values()) {
          String simpleName = unit.getAstNode().getName();
          List<String> packageName = unit.getAstNode().getPackageList();

          mtg.generateAdapter(Paths.get(targetFilepath.getAbsolutePath(),
            Names.getPathFromPackage(Names.getQualifiedName(packageName))).toFile(), packageName,
            simpleName);
        }
      }
    }
  }

  protected void generateTests(File modelPath, File testFilepath, File targetFilepath, File hwcPath,
    ComponentTypeSymbol comp, ConfigParams config) {
    if (testFilepath != null && targetFilepath != null && comp != null) {
      /* ============================================================ */
      /* ====== Copy generated-sources to generated-test-sources ==== */
      /* ============================================================ */
      copyGeneratedToTarget(targetFilepath);
      copyTestToTarget(testFilepath, targetFilepath, comp);
      if (ComponentHelper.isApplication(comp, config)) {
        Path target = Paths.get(Paths.get(targetFilepath.getAbsolutePath()).getParent().toString(),
          "generated-test-sources");
        File libraryPath = Paths.get(target.toString(), "montithings-RTE").toFile();
        // Check for Subpackages
        File[] subPackagesPath = getSubPackagesPath(modelPath.getAbsolutePath());

        // 6 generate make file
        if (config.getTargetPlatform() != ConfigParams.TargetPlatform.ARDUINO) {
          // Arduino uses its own build system
          mtg.generateTestMakeFile(target.toFile(), comp, libraryPath, subPackagesPath);
        }
      }
    }
  }

  /**
   * Initializes generation for a C++ port,
   * if appropriate C++ code templates are provided,
   *
   * @param target target directory for all artifacts.
   * @param config Generator configuration
   * @param comp   Component containing the ports for which C++ code should be generated.
   */
  public void generateHwcPort(File target, ConfigParams config, ComponentTypeSymbol comp) {
    for (PortSymbol port : comp.getPorts()) {
      if (config.getTemplatedPorts().contains(port)) {
        Optional<String> portType = GeneratorHelper.getPortHwcTemplateName(port, config);
        if (portType.isPresent()) {
          MTGenerator.generateAdditionalPort(config.getHwcTemplatePath(), target, portType.get(),
            config, port);
        }
      }
    }
  }

  protected ComponentTypeSymbol modelToSymbol(String model, IMontiThingsScope symTab) {
    String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
    return symTab.resolveComponentType(qualifiedModelName).get();
  }
}
