// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen;

import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import montithings.generator.codegen.util.Identifier;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.FileHelper;
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
      generateBehaviorImplementation(comp, targetPath, compname, existsHWC);
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

  public static void generateBehaviorImplementation(ComponentTypeSymbol comp, File targetPath, String compname, boolean existsHWC) {
    if (!existsHWC) {
      toFile(targetPath, compname + "Impl","template/behavior/implementation/ImplementationHeader.ftl"
        ,".h",comp, compname, existsHWC);
      toFile(targetPath, compname + "Impl",
          "template/behavior/implementation/ImplementationFile.ftl",".cpp", comp, compname, existsHWC);
    } else {
      toFile(targetPath, compname + "ImplTOP","template/behavior/implementation/ImplementationHeader.ftl"
          ,".h",comp, compname, existsHWC);
      toFile(targetPath, compname + "ImplTOP",
          "template/behavior/implementation/ImplementationFile.ftl",".cpp", comp, compname, existsHWC);
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

    if (config.getMessageBroker() == ConfigParams.MessageBroker.DDS) {
      toFile(targetPath, "dcpsconfig", "template/util/dds/DCPSConfig.ftl", ".ini", config);
    }

    toFile(targetPath, "run", "template/util/scripts/RunScript.ftl", ".sh", comp, config);
    toFile(targetPath, "kill", "template/util/scripts/KillScript.ftl", ".sh", sortedDirs);

    makeExecutable(targetPath, "run", ".sh");
    makeExecutable(targetPath, "kill", ".sh");
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
      toFile(path.toFile(), prefix + "." + comp.getName(), "template/util/comm/PortJson.ftl", ".json",comp.getType().getLoadedSymbol(), config, prefix + "." + comp.getName());
      for (ComponentInstanceSymbol subcomp : comp.getType().getLoadedSymbol().getSubComponents()) {
        generatePortJson(targetPath, subcomp, config, prefix + "." + comp.getName());
      }
    }
  }

  public static void generateAdapter(File targetPath, List<String> packageName, String simpleName, ConfigParams config) {
      toFile(targetPath, simpleName + "AdapterTOP", "template/adapter/Header.ftl", ".h",packageName, simpleName, config);
      toFile(targetPath, simpleName + "AdapterTOP", "template/adapter/ImplementationFile.ftl", ".cpp",packageName, simpleName, config);
    }

  public static void generateAdditionalPort(Path templatePath, File targetPath, String portName) {
    Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + StringUtils.capitalize(Names.getSimpleName(portName) + ".h"));
    if(!path.toFile().exists()||!path.toFile().isFile()) {
      Log.debug("Writing to file " + path + ".","");
      GeneratorSetup setup = new GeneratorSetup();
      setup.setTracing(false);
      setup.setAdditionalTemplatePaths(Collections.singletonList(templatePath.toFile().getAbsoluteFile()));

      Set<File> templates = FileHelper.getPortImplementation(Paths.get(templatePath.toFile().getAbsolutePath(),Names.getPathFromPackage(Names.getQualifier(portName))).toFile(),Names.getSimpleName(portName));
      for (File template:templates) {
        if(template.getName().endsWith(Names.getSimpleName(portName)+"Include.ftl")){
          setup.getGlex().bindTemplateHookPoint("<CppBlock>?portTemplate:include", portName+"Include");
        }
        else if(template.getName().endsWith(Names.getSimpleName(portName)+"Body.ftl")){
          setup.getGlex().bindTemplateHookPoint("<CppBlock>?portTemplate:body", portName+"Body");
        }
        else if(template.getName().endsWith(Names.getSimpleName(portName)+"GetExternalMessages.ftl")){
          setup.getGlex().bindTemplateHookPoint("<CppBlock>?portTemplate:getExternalMessages", portName+"GetExternalMessages");
        }
        else if(template.getName().endsWith(Names.getSimpleName(portName)+"SendToExternal.ftl")){
          setup.getGlex().bindTemplateHookPoint("<CppBlock>?portTemplate:sendToExternal", portName+"SendToExternal");
        }
      }

      GeneratorEngine engine = new GeneratorEngine(setup);

      engine.generateNoA("template/util/ports/sensorActuatorPort.ftl", path, portName);
    }
 }
}
