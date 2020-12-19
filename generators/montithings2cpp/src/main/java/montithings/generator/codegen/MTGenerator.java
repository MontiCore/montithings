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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
  Main entry point for generator. From this all target artifacts are generated for a component.
  It uses dispatching for calling the right implementation generator.
 **/
public class MTGenerator {

  public static void generateAll(File targetPath, File hwc, ComponentTypeSymbol comp, String compname, ConfigParams config, boolean generateDeploy) {
    Identifier.createInstance(comp);

    boolean useWsPorts = (config.getSplittingMode() != ConfigParams.SplittingMode.OFF && generateDeploy);

    toFile(targetPath, compname + "Input", "template/input/InputHeader.ftl", ".h", comp, compname, config);
    toFile(targetPath, compname + "Input", "template/input/ImplementationFile.ftl", ".cpp", comp, compname, config);
    toFile(targetPath, compname + "Result", "template/result/ResultHeader.ftl", ".h", comp, compname, config);
    toFile(targetPath, compname + "Result", "template/result/ImplementationFile.ftl", ".cpp",comp, compname, config);
    toFile(targetPath, compname, "template/componentGenerator/Header.ftl", ".h", comp, compname, config, useWsPorts);
    toFile(targetPath, compname, "template/componentGenerator/ImplementationFile.ftl", ".cpp", comp, compname, config, useWsPorts);

    if (comp.isAtomic()) {
      boolean existsHWC = FileHelper.existsHWCClass(hwc, comp.getPackageName() + "." + compname);
      generateBehaviorImplementation(comp, config, targetPath, compname, existsHWC);
    }

    // Generate inner components
    for(ComponentTypeSymbol innerComp : comp.getInnerComponents()) {
      //TODO Fix hwc path for inner components

      generateAll(targetPath.toPath().resolve(compname + "-Inner").toFile(), hwc, innerComp, innerComp.getName(), config, false);
    }

    // Generate deploy class
    if (ComponentHelper.isApplication(comp) || (config.getSplittingMode() != ConfigParams.SplittingMode.OFF && generateDeploy)) {
      if (config.getTargetPlatform() == ConfigParams.TargetPlatform.ARDUINO) {
        File sketchDirectory = new File(targetPath.getParentFile().getPath() + File.separator + "Deploy" + compname);
        sketchDirectory.mkdir();
        toFile(sketchDirectory, "Deploy" + compname, "template/deploy/DeployArduino.ftl",".ino",comp, compname);
        toFile(targetPath.getParentFile(), "README", "template/util/arduinoReadme/ArduinoReadme.ftl",".txt", targetPath.getName(), compname);
      } else {
        toFile(targetPath, "Deploy" + compname, "template/deploy/Deploy.ftl",".cpp",comp, compname, config);
        if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
          if (config.getMessageBroker() == ConfigParams.MessageBroker.OFF) {
            toFile(targetPath, compname + "Manager", "template/util/comm/Header.ftl", ".h", comp, config);
            toFile(targetPath, compname + "Manager", "template/util/comm/ImplementationFile.ftl", ".cpp", comp, config);
          } else if (config.getMessageBroker() == ConfigParams.MessageBroker.DDS) {
            toFile(targetPath, compname + "DDSParticipant", "template/util/dds/participant/Header.ftl", ".h", comp, config);
            toFile(targetPath, compname + "DDSParticipant", "template/util/dds/participant/ImplementationFile.ftl", ".cpp", comp, config);
          }
        }
      }
    }
  }

  public static void generateBehaviorImplementation(ComponentTypeSymbol comp, ConfigParams config, File targetPath, String compname, boolean existsHWC) {
    if (!existsHWC) {
      toFile(targetPath, compname + "Impl","template/behavior/implementation/ImplementationHeader.ftl"
        ,".h",comp, compname, config, existsHWC);
      toFile(targetPath, compname + "Impl",
          "template/behavior/implementation/ImplementationFile.ftl",".cpp", comp, compname, config, existsHWC);
    } else {
      toFile(targetPath, compname + "ImplTOP","template/behavior/implementation/ImplementationHeader.ftl"
          ,".h",comp, compname, config, existsHWC);
      toFile(targetPath, compname + "ImplTOP",
          "template/behavior/implementation/ImplementationFile.ftl",".cpp", comp, compname, config, existsHWC);
    }
  }

  static private void toFile(File targetPath, String name, String template, String fileExtension, Object... templateArguments) {
    Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + name + fileExtension);
      Log.debug("Writing to file " + path + ".","MTGenerator");
      GeneratorSetup setup = new GeneratorSetup();
      setup.setTracing(false);
      //setup.setAdditionalTemplatePaths(Collections.singletonList(new File("src/main/java/montithings/generator/codegen")));

      GeneratorEngine engine = new GeneratorEngine(setup);

      engine.generateNoA(template, path, templateArguments);
  }

  static private void makeExecutable(File targetPath, String name, String fileExtension) {
    Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + name + fileExtension);
    path.toFile().setExecutable(true);
  }

  public static void generateBuildScript(File targetPath, ConfigParams config) {
    toFile(targetPath, "build", "template/util/scripts/BuildScript.ftl", ".sh",config);
    makeExecutable(targetPath, "build", ".sh");
    toFile(targetPath, "build", "template/util/scripts/WinBuildScript.ftl", ".bat",config);
    makeExecutable(targetPath, "build", ".bat");

    toFile(targetPath, "reformatCode", "template/util/scripts/ReformatScript.ftl", ".sh");
    toFile(targetPath, "", "template/util/scripts/ClangFormat.ftl", ".clang-format");
    makeExecutable(targetPath, "reformatCode", ".sh");

    generateDDSDCPSConfig(targetPath, config);
  }

  public static void generateDockerfileScript(File targetPath, ComponentTypeSymbol comp, ConfigParams config) {
    toFile(targetPath, "Dockerfile", "template/util/scripts/DockerfileScript.ftl", "", comp, config);
    toFile(targetPath, "dockerBuild", "template/util/scripts/DockerBuild.ftl", ".sh", comp, config);
    makeExecutable(targetPath, "dockerBuild", ".sh");
    toFile(targetPath, "dockerRun", "template/util/scripts/DockerRun.ftl", ".sh", comp, config);
    makeExecutable(targetPath, "dockerRun", ".sh");
  }
  

  public static void generateMakeFile(File targetPath, ComponentTypeSymbol comp, File hwcPath, File libraryPath, File[] subPackagesPath, ConfigParams config){
  toFile(targetPath, "CMakeLists", "template/util/cmake/TopLevelCMake.ftl", ".txt",targetPath.listFiles(),
      comp,
      targetPath.toPath().toAbsolutePath().relativize(hwcPath.toPath().toAbsolutePath()).toString(),
      targetPath.toPath().toAbsolutePath().relativize(libraryPath.toPath().toAbsolutePath()).toString(),
      subPackagesPath, config, false);
  }

  public static void generateMakeFileForSubdirs(File targetPath, List<String> subdirectories) {
    List sortedDirs = new ArrayList<String>();
    sortedDirs.addAll(subdirectories);
    sortedDirs.sort(Comparator.naturalOrder());

    toFile(targetPath, "CMakeLists", "template/util/cmake/CMakeForSubdirectories.ftl", ".txt",sortedDirs);
  }

  public static void generateTestMakeFile(File targetPath, ComponentTypeSymbol comp, File hwcPath, File libraryPath, File[] subPackagesPath, ConfigParams config){
    toFile(Paths.get(targetPath.toString(),"test","gtests").toFile(),
      "CMakeLists", "template/util/cmake/GoogleTestParameters.ftl", ".txt",comp);
    toFile(targetPath, "CMakeLists", "template/util/cmake/LinkTestLibraries.ftl", ".txt",targetPath.listFiles(),
        comp,
        targetPath.toPath().toAbsolutePath().relativize(hwcPath.toPath().toAbsolutePath()).toString(),
        targetPath.toPath().toAbsolutePath().relativize(libraryPath.toPath().toAbsolutePath()).toString(),
        subPackagesPath, config, true);
  }

  public static void generateScripts(File targetPath, ComponentTypeSymbol comp, ConfigParams config, List<String> subdirectories) {
    List sortedDirs = new ArrayList<String>();
    sortedDirs.addAll(subdirectories);
    sortedDirs.sort(Comparator.naturalOrder());

    toFile(targetPath, "run", "template/util/scripts/RunScript.ftl", ".sh", comp, config);
    toFile(targetPath, "kill", "template/util/scripts/KillScript.ftl", ".sh", sortedDirs, config);
    
    makeExecutable(targetPath, "run", ".sh");
    makeExecutable(targetPath, "kill", ".sh");
  }

  public static void generateDDSDCPSConfig(File targetPath, ConfigParams config) {
    toFile(targetPath, "dcpsconfig", "template/util/dds/DCPSConfig.ftl", ".ini", config);
  }

  public static void generateTestScript(File targetPath, ConfigParams config) {
    toFile(targetPath, "runTests", "template/util/scripts/RunTests.ftl", ".sh", config);
    makeExecutable(targetPath, "runTests", ".sh");
  }

  public static void generatePortJson(File targetPath, ComponentTypeSymbol comp, ConfigParams config) {
    if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
      Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + "ports");
      toFile(path.toFile(), comp.getFullName(), "template/util/comm/PortJson.ftl", ".json",comp, config, comp.getFullName());
      for (ComponentInstanceSymbol subcomp : comp.getSubComponents()) {
        generatePortJson(targetPath, subcomp, config, comp.getFullName());
      }
    }
  }

  public static void generatePortJson(File targetPath, ComponentInstanceSymbol comp, ConfigParams config, String prefix) {
    if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
      Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + "ports");
      toFile(path.toFile(), prefix + "." + comp.getName(), "template/util/comm/PortJson.ftl", ".json",comp.getType(), config, prefix + "." + comp.getName());
      for (ComponentInstanceSymbol subcomp : comp.getType().getSubComponents()) {
        generatePortJson(targetPath, subcomp, config, prefix + "." + comp.getName());
      }
    }
  }

  public static void generateAdapter(File targetPath, List<String> packageName, String simpleName, ConfigParams config) {
      toFile(targetPath, simpleName + "AdapterTOP", "template/adapter/Header.ftl", ".h",packageName, simpleName, config);
      toFile(targetPath, simpleName + "AdapterTOP", "template/adapter/ImplementationFile.ftl", ".cpp",packageName, simpleName, config);
    }

  /**
   * Generates port artifact, based on template template/util/ports/sensorActuatorPort.ftl,
   * if the file does not already exists.
   * Hookpoints used by sensorActuatorPort.ftl will be bound, if possible.
   * @param templatePath Base directory for user given templates.
   * @param targetPath Directory to write the resulting C++ artifact to.
   * @param portName Qualified name of the resulting port. E.g. a.b.c.ComponentnamePortnamePort.
   * @param config Configuration of the generator. Mainly, the platform and port configuration is used.
   * @param portSymbol The port for which the C++ code is generated.
   */
  public static void generateAdditionalPort(Path templatePath, File targetPath, String portName, ConfigParams config, PortSymbol portSymbol) {
    Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + StringUtils.capitalize(Names.getSimpleName(portName) + ".h"));
    if(!path.toFile().exists()||!path.toFile().isFile()) {
      Log.debug("Writing to file " + path + ".","");
      // Template environment setup.
      GeneratorSetup setup = new GeneratorSetup();
      setup.setTracing(false);
      setup.setAdditionalTemplatePaths(Collections.singletonList(templatePath.toFile().getAbsoluteFile()));

      // Set of templates that follow a defined naming scheme that will be used if no specific template for a port is given.
      // The scheme follows the pattern templatePath/a/b/c/ComponentnamePortnamePort["Include|Body|Provide|Consume"].ftl,
      // if the portName equals a.b.c.ComponentnamePortnamePort.
      Set<File> templates = FileHelper.getPortImplementation(Paths.get(templatePath.toFile().getAbsolutePath(),Names.getPathFromPackage(Names.getQualifier(portName))).toFile(),Names.getSimpleName(portName));
      // Bind hookpoints to templates when possible, that will be used by the generator engine.
      bindSAPortTemplate(portName, setup, templates, "include", config, portSymbol);
      bindSAPortTemplate(portName, setup, templates, "body", config, portSymbol);
      bindSAPortTemplate(portName, setup, templates, "provide", config, portSymbol);
      bindSAPortTemplate(portName, setup, templates, "consume", config, portSymbol);

      // Port generation.
      GeneratorEngine engine = new GeneratorEngine(setup);
      engine.generateNoA("template/util/ports/sensorActuatorPort.ftl", path, portName);
    }
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
   * @param portName Qualified name of the resulting port. E.g. a.b.c.ComponentnamePortnamePort.
   * @param setup Generator setup where hookpoints are bound and global variables are set.
   * @param templates Templates following a defined naming scheme.
   * @param hookpoint Hookpoint name that should be bound by this method.
   * @param config Configuration of the generator. Mainly, the platform and port configuration is used.
   * @param portSymbol Port that may have specific templates configured for usage.
   */
  private static void bindSAPortTemplate(String portName, GeneratorSetup setup, Set<File> templates, String hookpoint, ConfigParams config, PortSymbol portSymbol) {
    hookpoint = StringUtils.capitalize(hookpoint);
    // Get specified template for the port.
    Optional<HookpointSymbol> hookpointSymbol = Optional.empty();
    if(!(config.getMtConfigScope()==null)){
      hookpointSymbol = config.getMtConfigScope().resolveHookpoint(config.getTargetPlatform().name(),portSymbol,StringUtils.uncapitalize(hookpoint));
    }
    // Bind specified template, if present and set arguments for it as global variable.
    if(hookpointSymbol.isPresent()){
      String templateName = Names.getQualifier(portName)+"."+hookpointSymbol.get().getAstNode().getTemplate();
      templateName = templateName.substring(0,templateName.lastIndexOf(".ftl"));
      setup.getGlex().bindTemplateHookPoint("<CppBlock>?portTemplate:" + StringUtils.uncapitalize(hookpoint), templateName);
      if(hookpointSymbol.get().getAstNode().isPresentArguments()){
        setup.getGlex().defineGlobalVar("globalVar"+hookpoint,hookpointSymbol.get().getAstNode().getArguments());
      }
    }
    // Bind given templates following the default naming scheme.
    else {
      for (File template : templates) {
        if (template.getName().endsWith(Names.getSimpleName(portName) + hookpoint + ".ftl")) {
          setup.getGlex().bindTemplateHookPoint("<CppBlock>?portTemplate:" + StringUtils.uncapitalize(hookpoint), portName + hookpoint);
          break;
        }
      }
    }
  }
}
