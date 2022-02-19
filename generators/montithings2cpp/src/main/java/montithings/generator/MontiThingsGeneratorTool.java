// (c) https://github.com/MontiCore/monticore
package montithings.generator;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentTypeSymbolTOP;
import arcbasis._symboltable.PortSymbol;
import behavior._ast.ASTConnectStatement;
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
import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.ModelPath;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.util.Modelfinder;
import montithings.MontiThingsMill;
import montithings.MontiThingsTool;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings._symboltable.IMontiThingsScope;
import montithings._symboltable.MontiThingsGlobalScope;
import montithings._visitor.MontiThingsTraverser;
import montithings.cocos.PortConnection;
import montithings.generator.cd2cpp.CppGenerator;
import montithings.generator.cocos.ComponentHasBehavior;
import montithings.generator.codegen.ConfigParams;
import montithings.generator.codegen.ConfigParams.MessageBroker;
import montithings.generator.codegen.ConfigParams.SplittingMode;
import montithings.generator.codegen.MTGenerator;
import montithings.generator.data.Models;
import montithings.generator.helper.CD4MTTool;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.GeneratorHelper;
import montithings.generator.visitor.FindConnectStatementsVisitor;
import montithings.generator.visitor.FindTemplatedPortsVisitor;
import montithings.generator.visitor.GenericInstantiationVisitor;
import montithings.trafos.*;
import montithings.util.MontiThingsError;
import mtconfig.MTConfigTool;
import mtconfig._ast.ASTMTConfigUnit;
import mtconfig._cocos.MTConfigCoCos;
import mtconfig._parser.MTConfigParser;
import mtconfig._symboltable.IMTConfigGlobalScope;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static montithings.generator.helper.ComponentHelper.getInterfaceClassNames;
import static montithings.generator.helper.FileHelper.*;

public class MontiThingsGeneratorTool extends MontiThingsTool {

  public static final String TOOL_NAME = "MontiThingsGeneratorTool";

  protected MTGenerator mtg;

  protected boolean stopAfterCoCoCheck = false;

  public void generate(File modelPath, File target, File hwcPath, File testPath,
    ConfigParams config) {

    ModelPath mp = new ModelPath(modelPath.toPath());
    mtg = new MTGenerator(target, hwcPath, config);

    /* ============================================================ */
    /* ==================== Copy HWC to target ==================== */
    /* ============================================================ */
    copyHwcToTarget(target, hwcPath, config);
    copyDeploymentConfigToTarget(target, hwcPath);


    /* ============================================================ */
    /* ======================== Find Models ======================= */
    /* ============================================================ */
    Models models = new Models(modelPath);

    /* ============================================================ */
    /* ======================= Serialize CDs ====================== */
    /* ============================================================ */

    String symbolPath = target.toString() + File.separator + "symbols" + File.separator;

    // Ensure symbols folder exists
    File symbolsFolder = new File(symbolPath);
    if (!symbolsFolder.exists()) {
      if (!symbolsFolder.mkdirs()) {
        Log.error("0xMT1200 Could not create directory '" + symbolsFolder.getAbsolutePath() + "'");
      }
    }

    if (!models.getClassdiagrams().isEmpty()) {
      CD4MTTool.convertToSymFile(modelPath, models.getClassdiagrams(), symbolPath);
      mp.addEntry(Paths.get(symbolPath));
    }

    /* ============================================================ */
    /* ===================== Set up Symbol Tabs =================== */
    /* ============================================================ */
    Log.info("Initializing symboltable", TOOL_NAME);

    if (config.getReplayMode() == ConfigParams.ReplayMode.ON) {
      addTrafo(new ExternalPortMockTrafo(modelPath, config.getReplayDataFile(),
        config.getMainComponent()));
      addTrafo(new DelayedChannelTrafo(modelPath, config.getReplayDataFile()));
      addTrafo(new DelayedComputationTrafo(modelPath, config.getReplayDataFile()));
    }

    addTrafo(new SimplifyStatechartTrafo());

    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    ICD4CodeGlobalScope cd4MTGlobalScope = CD4CodeMill.globalScope();
    cd4MTGlobalScope.setModelPath(mp);

    if (config.getPortNameTrafo() == ConfigParams.PortNameTrafo.ON) {
      ComponentTypePortsNamingTrafo typePortsNamingTrafo = new ComponentTypePortsNamingTrafo(config.getTemplatedPorts());
      addTrafo(typePortsNamingTrafo);
    }

    MontiThingsMill.reset();
    MontiThingsMill.init();
    MontiThingsMill.globalScope().clear();
    IMontiThingsGlobalScope symTab = createMTGlobalScope(mp);
    CD4CodeGlobalScope componentTypeScopes = createClassDiagrams(
      (MontiThingsGlobalScope) symTab, symbolPath);
    if (models.getClassdiagrams().isEmpty()) {
      mp.addEntry(Paths.get(symbolPath));
    }
    createSymbolTable(symTab);
    // ports introduced by the ComponentTypePortsNamingTrafo have to be added in class diagrams
    if (config.getPortNameTrafo() == ConfigParams.PortNameTrafo.ON) {
      componentTypeScopes = createMissingClassDiagrams(
        (MontiThingsGlobalScope) symTab, symbolPath);
    }

    // generate here, as CD4CodeGlobalScope is reset by CDLangExtension symbol table
    generateComponentTypeCDs(componentTypeScopes, target);

    CDLangExtensionTool cdExtensionTool = new CDLangExtensionTool();
    ICDLangExtensionGlobalScope cdLangExtensionGlobalScope = cdExtensionTool.initSymbolTable(
      modelPath);
    config.setCdLangExtensionScope(cdLangExtensionGlobalScope);

    BindingsTool bindingsTool = new BindingsTool();
    bindingsTool.setMtGlobalScope(symTab);
    IBindingsGlobalScope binTab = bindingsTool.initSymbolTable(modelPath);

    MTConfigTool mtConfigTool = new MTConfigTool();
    mtConfigTool.setMtGlobalScope(symTab);
    IMTConfigGlobalScope mtConfigGlobalScope = mtConfigTool.initSymbolTable(modelPath);
    config.setMtConfigScope(mtConfigGlobalScope);
    mtConfigTool.processFiles(models.getMTConfig());

    /* ============================================================ */
    /* =================== Find Code Templates ==================== */
    /* ============================================================ */

    Log.info("Looking for code templates", TOOL_NAME);

    GenericInstantiationVisitor genericInstantiationVisitor = new GenericInstantiationVisitor();

    for (String model : models.getMontithings()) {
      // Parse model
      String qualifier = Names.getQualifier(model);
      String qualifiedModelName = qualifier + (qualifier.isEmpty() ? "" : ".")
        + Names.getSimpleName(model);
      ComponentTypeSymbol comp = symTab.resolveComponentType(qualifiedModelName).get();

      Log.info("Searching templates for: " + comp.getFullName(), TOOL_NAME);

      // Find ports with templates
      FindTemplatedPortsVisitor visitor = new FindTemplatedPortsVisitor(config);
      comp.getAstNode().accept(visitor.createTraverser());
      config.getTemplatedPorts().addAll(visitor.getTemplatedPorts());

      comp.getAstNode().accept(genericInstantiationVisitor.createTraverser());
    }

    config.setTypeArguments(genericInstantiationVisitor.getTypeArguments());

    /* ============================================================ */
    /* ====================== Check Models ======================== */
    /* ============================================================ */
    Log.info("Checking models", TOOL_NAME);
    MontiThingsMill.reset();
    MontiThingsMill.init();
    BasicSymbolsMill.initializePrimitives();
    // add generator CoCos
    checker.addCoCo(new ComponentHasBehavior(config.getHwcPath()));
    checker.addCoCo(new PortConnection(config.getTemplatedPorts()));
    checkCoCos(symTab);
    checkIfMainComponentExists(symTab, models, config);
    checkCdExtensionModels(models.getCdextensions(), modelPath, config, cdExtensionTool);
    checkBindings(models.getBindings(), config, bindingsTool, binTab);
    checkMTConfig(models.getMTConfig(), config, mtConfigTool, mtConfigGlobalScope);

    if (stopAfterCoCoCheck) {
      return;
    }

    /* ============================================================ */
    /* =============== Generate SensorActuatorPorts =============== */
    /* ============================================================ */
    File[] packages = hwcPath.listFiles();
    List<String> executableSensorActuatorPorts = new ArrayList<>();

    for (File pckg : Objects.requireNonNull(packages)) {
      Set<String> sensorActuatorPorts = getFilesWithEnding(
        new File(hwcPath + File.separator + pckg.getName()), getFileEndings());
      for (String port : sensorActuatorPorts) {
        if (!templatePortBelongsToComponent(symTab, port, config)) {
          mtg.generateSensorActuatorPort(port, pckg.getName(), config);
          generateCMakeForSensorActuatorPort(pckg.getName(), port, config);
          executableSensorActuatorPorts.add(pckg.getName() + "." + port);
        }
      }
    }

    if (!executableSensorActuatorPorts.isEmpty()
      && config.getSplittingMode() == SplittingMode.OFF) {
      Log.error("Cannot use SplittingMode OFF with SensorActuatorPorts");
    }

    List<String> hwcPythonScripts = new ArrayList<>();
    for (File pckg : packages) {
      Set<String> pythonScriptsWithoutPckg = getFilesWithEnding(
        new File(hwcPath + File.separator + pckg.getName()),
        Stream.of(".py").collect(Collectors.toSet())
      );
      for (String script : pythonScriptsWithoutPckg) {
        hwcPythonScripts.add(pckg.getName() + "." + script);
      }
    }

    /* ============================================================ */
    /* ====================== Generate Code ======================= */
    /* ============================================================ */

    if (config.getReplayMode() == ConfigParams.ReplayMode.ON) {
      // clear list of templated ports since they get mocked by a trafo
      config.getTemplatedPorts().clear();

      List<String> allModels = symTab.getSubScopes().stream()
        .map(s -> s.getComponentTypeSymbols().values())
        .flatMap(Collection::stream)
        .map(ComponentTypeSymbolTOP::getFullName)
        .collect(Collectors.toList());
      models.setMontithings(allModels);
    }

    // Collect all the instances of the executable components (Some components
    // may only be included in other components and thus do not need an own
    // executable).
    ComponentTypeSymbol mainCompSymbol = modelToSymbol(config.getMainComponent(), symTab);
    List<Pair<ComponentTypeSymbol, String>> instances = ComponentHelper
      .getExecutableInstances(mainCompSymbol, config);
    HashSet<ComponentTypeSymbol> executableComponents = new HashSet<>();
    for (Pair<ComponentTypeSymbol, String> instance : instances) {
      executableComponents.add(instance.getKey());
    }

    // Also generate code for all components that are never used directly
    // whose interface is exchanged dynamically via a port (i.e. components
    // that may be instantiated dynamically)
    for (ComponentTypeSymbol cs : getAllComponents(symTab)) {
      if (componentIsUsedDynamically(cs, symTab)) {
        executableComponents.add(cs);
      }
    }

    // Aggregate all the target folders for the components.
    List<String> executableSubdirs = new ArrayList<>(instances.size());
    for (ComponentTypeSymbol comp : executableComponents) {
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
            Log.debug("Including model \"" + sub.getFullName() + "\" with deployment of \""
              + comp.getFullName() + "\"", TOOL_NAME);
            includeModels.add(sub);
          }
        }
      }
    }

    if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
      mtg.generateMakeFileForSubdirs(target, executableSubdirs, executableSensorActuatorPorts,
        config);
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

      Set<ComponentTypeSymbol> dynConnectedSubcomps = getDynamicallyConnectedSubcomps(e.getKey());

      // Save splitting mode and message broker for overriding it for subcomponents that should be included in the same binary.
      SplittingMode orgSplit = config.getSplittingMode();
      MessageBroker orgBroker = config.getMessageBroker();

      for (ComponentTypeSymbol symModel : enclosingModels) {
        String model = symModel.getFullName();
        boolean genDeploy = model.equals(baseModel);

        // Only the deployed component should communicate directly with the 'outer world'.
        // All the other enclosed components should communicate using native ports.
        // Unless its dynamically connected. Then it needs to communicate.
        config.setSplittingMode(genDeploy ? orgSplit : SplittingMode.OFF);
        if (!dynConnectedSubcomps.contains(symModel)) {
          config.setMessageBroker(genDeploy ? orgBroker : MessageBroker.OFF);
        }

        generateCppForComponent(model, symTab, compTarget, hwcPath, config, models, genDeploy);

        if (!genDeploy) {
          // copy hwc for embedded component manually
          copyHwcToTarget(new File(target, baseModel), hwcPath, model, config, models);
        }
      }
      // reset splitting mode and message broker
      config.setSplittingMode(orgSplit);
      config.setMessageBroker(orgBroker);

      generateCMakeForComponent(baseModel, symTab, modelPath, compTarget, config,
        executableSensorActuatorPorts, hwcPythonScripts, executableSubdirs);

      mtg = new MTGenerator(target, hwcPath, config);
    }

    if (config.getSplittingMode() == ConfigParams.SplittingMode.OFF) {
      generateCDEAdapter(target, config);
    }

    generateCD(modelPath, hwcPath, target);

    for (String model : models.getMontithings()) {
      ComponentTypeSymbol comp = modelToSymbol(model, symTab);
      if (ComponentHelper.isApplication(comp, config)) {
        mtg.generateBuildScript(target, comp, hwcPythonScripts);
        mtg.generateDockerfileScript(target, comp, executableSensorActuatorPorts, hwcPythonScripts);
        mtg.generateCrosscompileScript(target, comp);
      }
    }

    generateDeployInfo(target, config, instances);

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
      .map(c -> Names.getQualifier(c) + (Names.getQualifier(c).isEmpty()? "" : ".") + Names.getSimpleName(c))
      .collect(Collectors.toList());

    String allComponents = String.join(", ", allComponentsList);

    if (!symTab.resolveComponentType(config.getMainComponent()).isPresent()) {
      Log.error(String.format(MontiThingsError.GENERATOR_MAIN_UNKNOWN.toString(),
        config.getMainComponent(), allComponents)
      );
    }
  }

  protected ComponentTypeSymbol getMainComponent (IMontiThingsGlobalScope symTab,
      ConfigParams configParams){
    return symTab.resolveComponentType(configParams.getMainComponent()).get();
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
      config.setMtConfigScope(mtConfigTool.createSymboltable(ast, symTab));

      // check cocos
      Log.info("Check model: " + model, "MontiThingsGeneratorTool");
      MTConfigCoCos.createChecker().checkAll(ast);
    }
  }

  /* ============================================================ */
  /* ===================== Generate Code ======================== */
  /* ============================================================ */

  protected void generateCppForComponent(String model, IMontiThingsScope symTab, File target,
    File hwcPath, ConfigParams config, Models models) {
    generateCppForComponent(model, symTab, target, hwcPath, config, models, true);
  }

  protected void generateCppForComponent(String model, IMontiThingsScope symTab, File target,
    File hwcPath, ConfigParams config, Models models, boolean generateDeploy) {
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
      copyHwcToTarget(target, hwcPath, model, config, models);
    }
  }

  protected void generateCMakeForComponent(String model, IMontiThingsScope symTab, File modelPath,
    File target, ConfigParams config, List<String> sensorActuatorPorts,
    List<String> hwcPythonScripts, List<String> executableInstanceNames) {
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
        mtg.generateMakeFile(target, comp, libraryPath, subPackagesPath, sensorActuatorPorts);
        if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
          mtg.generateScripts(target, comp, sensorActuatorPorts, hwcPythonScripts,
            executableInstanceNames);
        }
      }
    }
  }

  protected void generateCMakeForSensorActuatorPort(String pckg, String port, ConfigParams config) {
    // 6 generate make file
    if (config.getTargetPlatform()
      != ConfigParams.TargetPlatform.ARDUINO) { // Arduino uses its own build system
      Log.info("Generate CMake file for " + port, "MontiThingsGeneratorTool");
      mtg.generateMakeFileForSensorActuatorPort(pckg, port, "montithings-RTE");
    }
  }

  protected void generateComponentTypeCDs(CD4CodeGlobalScope scopes, File targetFilepath) {
    for (ICD4CodeScope scope : scopes.getSubScopes()) {
      String modelName = scope.getName();
      Log.info("Generate ComponentType model: " + modelName, TOOL_NAME);
      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new CppGenerator(outDir, scope)
        .generate(Optional.empty());
    }
  }

  protected void generateCD(File modelPath, File hwcPath, File targetFilepath) {
    List<String> foundModels = Modelfinder
      .getModelsInModelPath(modelPath, CD4AnalysisGlobalScope.EXTENSION);
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);

      Log.info("Generate CD model: " + model, TOOL_NAME);
      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new CppGenerator(outDir, Paths.get(modelPath.getAbsolutePath()),
        Paths.get(hwcPath.getAbsolutePath()), model)
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
        portType.ifPresent(s ->
          MTGenerator.generateAdditionalPort(config.getHwcTemplatePath(), target, s, config, port));
      }
    }
  }

  protected void generateDeployInfo(File target, ConfigParams config,
    List<Pair<ComponentTypeSymbol, String>> executableInstances) {
    JsonObjectBuilder jsonBase = Json.createObjectBuilder();

    // Collect executable instances.
    JsonArrayBuilder jsonInstances = Json.createArrayBuilder();

    for (Pair<ComponentTypeSymbol, String> instance : executableInstances) {
      // Each executable instance will be added to the "instances" array.
      ComponentTypeSymbol comp = instance.getKey();
      JsonObjectBuilder jsonInstance = Json.createObjectBuilder();

      jsonInstance.add("componentType", comp.getFullName());
      jsonInstance.add("instanceName", instance.getValue());
      jsonInstance.add("dockerImage", comp.getFullName().toLowerCase() + ":latest");

      // Also add the requirements of the component.
      JsonArrayBuilder jreqs = Json.createArrayBuilder();
      ComponentHelper.getRequirements(comp, config).forEach(jreqs::add);
      jsonInstance.add("requirements", jreqs.build());

      jsonInstances.add(jsonInstance);
    }
    jsonBase.add("instances", jsonInstances.build());

    // Serialize JSON and write it to a file.
    String jsonString = jsonBase.build().toString();
    File jsonFile = new File(target, "deployment-info.json");
    FileReaderWriter.storeInFile(jsonFile.getAbsoluteFile().toPath(), jsonString);
  }

  protected ComponentTypeSymbol modelToSymbol(String model, IMontiThingsScope symTab) {
    String qualifiedModelName = Names.getQualifier(model)
      + (Names.getQualifier(model).isEmpty()? "" : ".") + Names.getSimpleName(model);
    return symTab.resolveComponentType(qualifiedModelName).get();
  }

  public boolean isStopAfterCoCoCheck() {
    return stopAfterCoCoCheck;
  }

  public void setStopAfterCoCoCheck(boolean stopAfterCoCoCheck) {
    this.stopAfterCoCoCheck = stopAfterCoCoCheck;
  }

  public Set<ComponentTypeSymbol> getAllComponents(IMontiThingsGlobalScope symTab) {
    Set<ComponentTypeSymbol> allComponentTypes = new HashSet<>();
    for (IMontiThingsScope scope : symTab.getSubScopes()) {
      allComponentTypes.addAll(scope.getComponentTypeSymbols().values());
    }
    return allComponentTypes;
  }

  public Set<ComponentTypeSymbol> getDynamicallyConnectedSubcomps(ComponentTypeSymbol enclosingComp) {
    Set<ComponentTypeSymbol> result = new HashSet<>();

    // Find all connect statements
    FindConnectStatementsVisitor visitor = new FindConnectStatementsVisitor();
    Set<ASTBehavior> behaviors = enclosingComp.getAstNode().getBody().getArcElementList().stream()
      .filter(e -> e instanceof ASTBehavior)
      .map(e -> (ASTBehavior)e)
      .collect(Collectors.toSet());
    MontiThingsTraverser traverser = visitor.createTraverser();
    for (ASTBehavior b : behaviors) {
      b.accept(traverser);
    }

    // Get the types of all component instances accessed in connect statements
    for (ASTConnectStatement cs : visitor.getConnectStatements()) {
      Set<ASTPortAccess> portAccesses = new HashSet<>();
      portAccesses.add(cs.getConnector().getSource());
      portAccesses.addAll(cs.getConnector().getTargetList());

      for (ASTPortAccess pa : portAccesses) {
        if (pa.isPresentComponentSymbol()) {
          result.add(pa.getComponentSymbol().getType());
        }
      }
    }

    return result;
  }

  public boolean componentIsUsedDynamically(ComponentTypeSymbol component,
    IMontiThingsGlobalScope symTab) {

    Set<String> namesOfImplementedInterfaces = getInterfaceClassNames(component);

    for (ComponentTypeSymbol current : getAllComponents(symTab)) {
      if (current.getPorts().stream()
        .anyMatch(p -> namesOfImplementedInterfaces.contains(p.getTypeInfo().getName()))) {
        return true;
      }
    }

    return false;
  }

  public boolean templatePortBelongsToComponent(IMontiThingsGlobalScope symTab,
    String portName, ConfigParams config) {

    // Get all names of the FTL files for templating a port by file name
    Set<String> sensorActuatorPortNames = new HashSet<>();

    for (PortSymbol port : config.getTemplatedPorts()) {
      if (!port.getComponent().isPresent()) {
        Log.error(
          String.format("0xMT1112 Templated port '%s' has no component", port.getFullName()));
      }
      if (config.getTemplatedPorts().contains(port)) {
        sensorActuatorPortNames.add(
          StringUtils.capitalize(port.getComponent().get().getName()) +
            StringUtils.capitalize(port.getName()) +
            "Port"
        );
      }
    }

    // check if any of the templated ports matches the given port name
    return sensorActuatorPortNames.stream().anyMatch(portName::startsWith);
  }
}
