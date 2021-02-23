// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import montithings.generator.codegen.util.Identifier;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.FileHelper;
import mtconfig._symboltable.HookpointSymbol;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import prepostcondition._ast.ASTPostcondition;
import prepostcondition._ast.ASTPrecondition;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Main entry point for generator. From this all target artifacts are generated for a component.
 * It uses dispatching for calling the right implementation generator.
 **/
public class MTGenerator {

  protected File genSrcDir;
  protected File hwcDir;
  protected ConfigParams config;
  protected FileGenerator fg;

  public MTGenerator(@NotNull File genSrcDir, @NotNull File hwcDir, @NotNull ConfigParams config) {
    this.genSrcDir = genSrcDir;
    this.hwcDir = hwcDir;
    this.config = config;
    this.fg = new FileGenerator(genSrcDir, hwcDir);
  }

  public void generateAll(File targetPath, ComponentTypeSymbol comp, boolean generateDeploy) {
    Identifier.createInstance(comp);
    String compname = comp.getName();

    boolean useWsPorts = (config.getSplittingMode() != ConfigParams.SplittingMode.OFF
      && generateDeploy);

    fg.generate(targetPath, compname + "Input", ".h",
      "template/input/InputHeader.ftl", comp, compname, config);
    fg.generate(targetPath, compname + "Input", ".cpp",
      "template/input/ImplementationFile.ftl", comp, compname, config);
    fg.generate(targetPath, compname + "Result", ".h",
      "template/result/ResultHeader.ftl", comp, compname, config);
    fg.generate(targetPath, compname + "Result", ".cpp",
      "template/result/ImplementationFile.ftl", comp, compname, config);
    fg.generate(targetPath, compname + "State", ".h",
      "template/state/StateHeader.ftl", comp, config);
    fg.generate(targetPath, compname + "State", ".cpp",
      "template/state/ImplementationFile.ftl", comp, config);
    fg.generate(targetPath, compname, ".h",
      "template/componentGenerator/Header.ftl", comp, compname, config, useWsPorts);
    fg.generate(targetPath, compname, ".cpp",
      "template/componentGenerator/ImplementationFile.ftl", comp, compname, config, useWsPorts);
    fg.generate(targetPath, compname + "Precondition", ".h",
      "template/prepostconditions/GeneralHeader.ftl", comp, config, true);
    fg.generate(targetPath, compname + "Precondition", ".cpp",
      "template/prepostconditions/GeneralImplementationFile.ftl", comp, config, true);
    fg.generate(targetPath, compname + "Postcondition", ".h",
      "template/prepostconditions/GeneralHeader.ftl", comp, config, false);
    fg.generate(targetPath, compname + "Postcondition", ".cpp",
      "template/prepostconditions/GeneralImplementationFile.ftl", comp, config, false);

    int preconditionNumber = 1;
    for (ASTPrecondition precondition : ComponentHelper.getPreconditions(comp)) {
      fg.generate(targetPath, compname + "Precondition" + preconditionNumber, ".h",
        "template/prepostconditions/SpecificHeader.ftl", comp, precondition, config, preconditionNumber, true);
      fg.generate(targetPath, compname + "Precondition" + preconditionNumber, ".cpp",
        "template/prepostconditions/SpecificImplementationFile.ftl", comp, precondition, config, preconditionNumber, true);
      preconditionNumber++;
    }

    int postconditionNumber = 1;
    for (ASTPostcondition postcondition : ComponentHelper.getPostconditions(comp)) {
      fg.generate(targetPath, compname + "Postcondition" + postconditionNumber, ".h",
        "template/prepostconditions/SpecificHeader.ftl", comp, postcondition, config, postconditionNumber, false);
      fg.generate(targetPath, compname + "Postcondition" + postconditionNumber, ".cpp",
        "template/prepostconditions/SpecificImplementationFile.ftl", comp, postcondition, config, postconditionNumber, false);
      postconditionNumber++;
    }

    if (comp.isAtomic()) {
      generateBehaviorImplementation(comp, targetPath);
    }

    // Generate inner components
    for (ComponentTypeSymbol innerComp : comp.getInnerComponents()) {
      //TODO Fix hwc path for inner components
      generateAll(targetPath.toPath().resolve(compname + "-Inner").toFile(), innerComp, false);
    }

    // Generate deploy class
    if (ComponentHelper.isApplication(comp, config) || (
      config.getSplittingMode() != ConfigParams.SplittingMode.OFF && generateDeploy)) {
      if (config.getTargetPlatform() == ConfigParams.TargetPlatform.ARDUINO) {
        File sketchDirectory = new File(
          targetPath.getParentFile().getPath() + File.separator + "Deploy" + compname);
        sketchDirectory.mkdir();
        fg.generate(sketchDirectory, "Deploy" + compname, ".ino",
          "template/deploy/DeployArduino.ftl", comp, compname);
        fg.generate(targetPath.getParentFile(),
          "README", ".txt", "template/util/arduinoReadme/ArduinoReadme.ftl", targetPath.getName(),
          compname);
      }
      else {
        fg.generate(targetPath, "Deploy" + compname, ".cpp", "template/deploy/Deploy.ftl",
          comp, compname, config);
        if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
          if (config.getMessageBroker() == ConfigParams.MessageBroker.OFF) {
            fg.generate(targetPath, compname + "Manager", ".h", "template/util/comm/Header.ftl",
              comp, config);
            fg.generate(targetPath, compname + "Manager",
              ".cpp",
              "template/util/comm/ImplementationFile.ftl", comp, config);
          }
          else if (config.getMessageBroker() == ConfigParams.MessageBroker.DDS) {
            fg.generate(targetPath,
              compname + "DDSParticipant", ".h", "template/util/dds/participant/Header.ftl", comp,
              config);
            fg.generate(targetPath,
              compname + "DDSParticipant", ".cpp",
              "template/util/dds/participant/ImplementationFile.ftl", comp, config);
          }
        }
      }
    }
  }

  public void generateBehaviorImplementation(ComponentTypeSymbol comp, File targetPath) {
    fg.generate(targetPath, comp.getName() + "Impl", ".h",
        "template/behavior/implementation/ImplementationHeader.ftl", comp, comp.getName(), config);
    fg.generate(targetPath, comp.getName() + "Impl", ".cpp",
        "template/behavior/implementation/ImplementationFile.ftl", comp, comp.getName(), config);
  }

  protected void makeExecutable(File targetPath, String name, String fileExtension) {
    Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + name + fileExtension);
    path.toFile().setExecutable(true);
  }

  public void generateBuildScript(File targetPath) {
    fg.generate(targetPath, "build", ".sh",
      "template/util/scripts/BuildScript.ftl", config);
    makeExecutable(targetPath, "build", ".sh");

    fg.generate(targetPath, "build", ".bat",
      "template/util/scripts/WinBuildScript.ftl", config);
    makeExecutable(targetPath, "build", ".bat");

    fg.generate(targetPath, "reformatCode", ".sh",
      "template/util/scripts/ReformatScript.ftl");
    makeExecutable(targetPath, "reformatCode", ".sh");

    fg.generate(targetPath, "", ".clang-format",
      "template/util/scripts/ClangFormat.ftl");

    generateDDSDCPSConfig(targetPath);
  }

  public void generateDockerfileScript(File targetPath, ComponentTypeSymbol comp) {
    fg.generate(targetPath, "Dockerfile", "",
      "template/util/scripts/DockerfileScript.ftl", comp, config);
    fg.generate(targetPath, "dockerBuild", ".sh",
      "template/util/scripts/DockerBuild.ftl", comp, config);
    makeExecutable(targetPath, "dockerBuild", ".sh");
    fg.generate(targetPath, "dockerRun", ".sh",
      "template/util/scripts/DockerRun.ftl", comp, config);
    makeExecutable(targetPath, "dockerRun", ".sh");
  }

  public void generateMakeFile(File targetPath, ComponentTypeSymbol comp, File libraryPath,
    File[] subPackagesPath) {
    fg.generate(targetPath, "CMakeLists", ".txt",
      "template/util/cmake/TopLevelCMake.ftl",
      targetPath.listFiles(),
      comp,
      targetPath.toPath().toAbsolutePath().relativize(hwcDir.toPath().toAbsolutePath()).toString(),
      targetPath.toPath().toAbsolutePath().relativize(libraryPath.toPath().toAbsolutePath())
        .toString(), subPackagesPath, config, false);
  }

  public void generateMakeFileForSubdirs(File targetPath, List<String> subdirectories, ConfigParams config) {
    List sortedDirs = new ArrayList<String>();
    sortedDirs.addAll(subdirectories);
    sortedDirs.sort(Comparator.naturalOrder());


    fg.generate(targetPath, "CMakeLists", ".txt",
      "template/util/cmake/CMakeForSubdirectories.ftl", sortedDirs, config);
  }

  public void generateTestMakeFile(File targetPath, ComponentTypeSymbol comp,
    File libraryPath, File[] subPackagesPath) {
    FileGenerator fg = new FileGenerator(targetPath, hwcDir);
    fg.generate(Paths.get(targetPath.toString(), "test", "gtests").toFile(), "CMakeLists", ".txt",
      "template/util/cmake/GoogleTestParameters.ftl", comp);
    fg.generate(targetPath, "CMakeLists", ".txt",
      "template/util/cmake/LinkTestLibraries.ftl",
      targetPath.listFiles(),
      comp,
      targetPath.toPath().toAbsolutePath().relativize(hwcDir.toPath().toAbsolutePath()).toString(),
      targetPath.toPath().toAbsolutePath().relativize(libraryPath.toPath().toAbsolutePath())
        .toString(), subPackagesPath, config, true);
  }

  public void generateScripts(File targetPath, ComponentTypeSymbol comp, List<String> subdirectories) {
    List<String> sortedDirs = new ArrayList<>(subdirectories);
    sortedDirs.sort(Comparator.naturalOrder());

    fg.generate(targetPath, "run", ".sh",
      "template/util/scripts/RunScript.ftl", comp, config);
    makeExecutable(targetPath, "run", ".sh");

    fg.generate(targetPath, "kill", ".sh",
      "template/util/scripts/KillScript.ftl", sortedDirs, config);
    makeExecutable(targetPath, "kill", ".sh");
  }

  public void generateDDSDCPSConfig(File targetPath) {
    fg.generate(targetPath, "dcpsconfig", ".ini",
      "template/util/dds/DCPSConfig.ftl", config);
  }

  public void generateTestScript(File targetPath) {
    FileGenerator fg = new FileGenerator(targetPath, targetPath);
    fg.generate(targetPath, "runTests", ".sh",
      "template/util/scripts/RunTests.ftl", config);
    makeExecutable(targetPath, "runTests", ".sh");
  }

  public void generatePortJson(File targetPath, ComponentTypeSymbol comp) {
    if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
      Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + "ports");
      fg.generate(path.toFile(), comp.getFullName(), ".json",
        "template/util/comm/PortJson.ftl", comp, config, comp.getFullName());
      for (ComponentInstanceSymbol subcomp : comp.getSubComponents()) {
        generatePortJson(targetPath, subcomp, comp.getFullName());
      }
    }
  }

  public void generatePortJson(File targetPath, ComponentInstanceSymbol comp, String prefix) {
    if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
      Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + "ports");
      fg.generate(path.toFile(), prefix + "." + comp.getName(), ".json",
        "template/util/comm/PortJson.ftl", comp.getType(), config, prefix + "." + comp.getName());
      for (ComponentInstanceSymbol subcomp : comp.getType().getSubComponents()) {
        generatePortJson(targetPath, subcomp, prefix + "." + comp.getName());
      }
    }
  }

  public void generateAdapter(File targetPath, List<String> packageName, String simpleName) {
    fg.generate(targetPath, simpleName + "Adapter", ".h",
      "template/adapter/Header.ftl", packageName, simpleName, config);
    fg.generate(targetPath, simpleName + "Adapter", ".cpp",
      "template/adapter/ImplementationFile.ftl", packageName, simpleName, config);
  }

  /**
   * Generates port artifact, based on template template/util/ports/sensorActuatorPort.ftl,
   * if the file does not already exists.
   * Hookpoints used by sensorActuatorPort.ftl will be bound, if possible.
   *
   * @param templatePath Base directory for user given templates.
   * @param targetPath   Directory to write the resulting C++ artifact to.
   * @param portName     Qualified name of the resulting port. E.g. a.b.c.ComponentnamePortnamePort.
   * @param config       Configuration of the generator. Mainly, the platform and port configuration is used.
   * @param portSymbol   The port for which the C++ code is generated.
   */
  public static void generateAdditionalPort(Path templatePath, File targetPath, String portName,
    ConfigParams config, PortSymbol portSymbol) {

    String packageName = Names.getPathFromPackage(Names.getQualifier(portName));
    String fileNameHwc = StringUtils.capitalize(Names.getSimpleName(portName) + ".h");

    File hwcFile = new File(targetPath +
      File.separator + "hwc" +
      File.separator + packageName +
      File.separator + fileNameHwc);
    boolean existsHWC = hwcFile.exists() && hwcFile.isFile();

    String fileNameGen = StringUtils.capitalize(Names.getSimpleName(portName) + (existsHWC?"TOP":"") + ".h");
    Path path = Paths.get(targetPath.getAbsolutePath() +
      File.separator + packageName +
      File.separator + fileNameGen);

    Log.debug("Writing to file " + path + ".", "");
    // Template environment setup.
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);
    setup.setAdditionalTemplatePaths(
      Collections.singletonList(templatePath.toFile().getAbsoluteFile()));

    // Set of templates that follow a defined naming scheme that will be used if no specific template for a port is given.
    // The scheme follows the pattern templatePath/a/b/c/ComponentnamePortnamePort["Include|Body|Provide|Consume"].ftl,
    // if the portName equals a.b.c.ComponentnamePortnamePort.
    Set<File> templates = FileHelper.getPortImplementation(Paths
        .get(templatePath.toFile().getAbsolutePath(),
          Names.getPathFromPackage(Names.getQualifier(portName))).toFile(),
      Names.getSimpleName(portName));
    // Bind hookpoints to templates when possible, that will be used by the generator engine.
    bindSAPortTemplate(portName, setup, templates, "include", config, portSymbol);
    bindSAPortTemplate(portName, setup, templates, "body", config, portSymbol);
    bindSAPortTemplate(portName, setup, templates, "provide", config, portSymbol);
    bindSAPortTemplate(portName, setup, templates, "consume", config, portSymbol);

    // Port generation.
    GeneratorEngine engine = new GeneratorEngine(setup);
    engine.generateNoA("template/util/ports/sensorActuatorPort.ftl", path, portName, existsHWC);

  }

  /**
   * Binds hookpints of the form "<CppBlock>?portTemplate:" to a corresponding template if present.
   * Templates specified by the port configuration are used.
   * The port configuration is accessed from the configuration of the generator.
   * If such an port configuration is provided that not only specifies the template,
   * but also defines values required by the specific template, these values are added to the GeneratorSetup as global variables.
   * In the specified template, the values are accessible under the variable name "globalVar"+hookpoint, depending on the given hookpoint name.
   * Warning: The arguments provided for a specific template are accessible by all specific and default templates that are used for C++ port generation.
   * If a global variable named "globalVar"+hookpoint is already defined an error is logged.
   * If no port configuration exists, the given templates will be used for the binding
   * if the templateName matches the given simple portName appended by the Hookpoint+".ftl".
   * If multiple such matches are provided, the first one found is used.
   * Arguments for these templates must not exists.
   * If no template is specified for the given hookpoint, no binding will occur.
   * The given setup is extended by the bindings and global variables as required.
   *
   * @param portName   Qualified name of the resulting port. E.g. a.b.c.ComponentnamePortnamePort.
   * @param setup      Generator setup where hookpoints are bound and global variables are set.
   * @param templates  Templates following a defined naming scheme.
   * @param hookpoint  Hookpoint name that should be bound by this method.
   * @param config     Configuration of the generator. Mainly, the platform and port configuration is used.
   * @param portSymbol Port that may have specific templates configured for usage.
   */
  private static void bindSAPortTemplate(String portName, GeneratorSetup setup, Set<File> templates,
    String hookpoint, ConfigParams config, PortSymbol portSymbol) {
    hookpoint = StringUtils.capitalize(hookpoint);
    // Get specified template for the port.
    Optional<HookpointSymbol> hookpointSymbol = Optional.empty();
    if (!(config.getMtConfigScope() == null)) {
      hookpointSymbol = config.getMtConfigScope()
        .resolveHookpoint(config.getTargetPlatform().name(), portSymbol,
          StringUtils.uncapitalize(hookpoint));
    }
    // Bind specified template, if present and set arguments for it as global variable.
    if (hookpointSymbol.isPresent()) {
      String templateName =
        Names.getQualifier(portName) + "." + hookpointSymbol.get().getAstNode().getTemplate();
      templateName = templateName.substring(0, templateName.lastIndexOf(".ftl"));
      setup.getGlex()
        .bindTemplateHookPoint("<CppBlock>?portTemplate:" + StringUtils.uncapitalize(hookpoint),
          templateName);
      if (hookpointSymbol.get().getAstNode().isPresentArguments()) {
        setup.getGlex().defineGlobalVar("globalVar" + hookpoint,
          hookpointSymbol.get().getAstNode().getArguments());
      }
    }
    // Bind given templates following the default naming scheme.
    else {
      for (File template : templates) {
        if (template.getName().endsWith(Names.getSimpleName(portName) + hookpoint + ".ftl")) {
          setup.getGlex()
            .bindTemplateHookPoint("<CppBlock>?portTemplate:" + StringUtils.uncapitalize(hookpoint),
              portName + hookpoint);
          break;
        }
      }
    }
  }
}
