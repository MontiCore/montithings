# (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.ComponentInstanceSymbol;
import de.monticore.io.FileReaderWriter;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jline.internal.Log;
import montithings.generator.codegen.ConfigParams;
/*import montithings.generator.codegen.xtend.behavior.Implementation
import montithings.generator.codegen.xtend.util.ArduinoReadme
import montithings.generator.codegen.xtend.util.CMake
import montithings.generator.codegen.xtend.util.Comm
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.codegen.xtend.util.Scripts*/
import montithings.generator.codegen.xtend.util.Identifier;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.FileHelper;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;

/**

  Main entry point for generator. From this all target artifacts are generated for a component.
  It uses dispatching for calling the right implementation generator.

  @author  Pfeiffer
  @version $Revision$,
           $Date$
 **/
class MTGenerator {

  static void generateAll(File targetPath, File hwc, ComponentTypeSymbol comp, String compname, ConfigParams config, boolean generateDeploy) {
    Identifier.createInstance(comp);

    boolean useWsPorts = (config.getSplittingMode() != ConfigParams.SplittingMode.OFF && generateDeploy);

    toFile(targetPath, compname + "Input", Input.generateInputHeader(comp, compname, config), ".h");
    toFile(targetPath, compname + "Input", Input.generateImplementationFile(comp, compname, config), ".cpp");
    toFile(targetPath, compname + "Result", Result.generateResultHeader(comp, compname, config), ".h");
    toFile(targetPath, compname + "Result", Result.generateImplementationFile(comp, compname, config), ".cpp");
    toFile(targetPath, compname, ComponentGenerator.generateHeader(comp, compname, config, useWsPorts), ".h");
    toFile(targetPath, compname, ComponentGenerator.generateImplementationFile(comp, compname, config, useWsPorts), ".cpp");
    
    if (comp.isAtomic) {
      <#assign boolean existsHWC = FileHelper.existsHWCClass(hwc, comp.packageName + "." + compname);>
      generateBehaviorImplementation(comp, targetPath, compname, existsHWC)
    }
    
    // Generate inner components
    for(innerComp : comp.innerComponents) {
      //TODO Fix hwc path for inner components
      
      generateAll(targetPath.toPath.resolve(compname + "-Inner").toFile, hwc, innerComp, innerComp.name, config, false);
    }
    
    // Generate deploy class
    if (ComponentHelper.isApplication(comp) || (config.getSplittingMode() != ConfigParams.SplittingMode.OFF && generateDeploy)) {
      if (config.getTargetPlatform() == ConfigParams.TargetPlatform.ARDUINO) {
        <#assign sketchDirectory = new File(targetPath.getParentFile().getPath() + File.separator + "Deploy" + compname);>
        sketchDirectory.mkdir();
        toFile(sketchDirectory, "Deploy" + compname, Deploy.generateDeployArduino(comp, compname),".ino");
        toFile(targetPath.getParentFile(), "README", ArduinoReadme.printArduinoReadme(targetPath.name, compname),".txt");
      } else {
        toFile(targetPath, "Deploy" + compname, Deploy.generateDeploy(comp, compname, config),".cpp");
        if (config.getSplittingMode() != ConfigParams.SplittingMode.OFF) {
          toFile(targetPath, compname + "Manager", Comm.generateHeader(comp, config), ".h");
          toFile(targetPath, compname + "Manager", Comm.generateImplementationFile(comp, config), ".cpp");
        }
      }
    }

  }

  static void generateBehaviorImplementation(ComponentTypeSymbol comp, File targetPath, String compname, boolean existsHWC) {
    if (!existsHWC) {
      toFile(targetPath, compname + "Impl",
        Implementation.generateImplementationHeader(comp, compname, existsHWC),".h")
      toFile(targetPath, compname + "Impl",
        Implementation.generateImplementationFile(comp, compname, existsHWC),".cpp")
    } else {
      toFile(targetPath, compname + "ImplTOP",
        Implementation.generateImplementationHeader(comp, compname, existsHWC),".h")
      toFile(targetPath, compname + "ImplTOP",
        Implementation.generateImplementationFile(comp, compname, existsHWC),".cpp")
    }
  }

  static private void toFile(File targetPath, String name, String template, String fileExtension) {
    Path path = Paths.get(targetPath.getAbsolutePath() + File.separator + name + fileExtension);
    Log.info("Writing to file " + path + ".");
    //FileReaderWriter.storeInFile(path, content);
    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);

    GeneratorEngine engine = new GeneratorEngine(setup);

    engine.generateNoA(template, path);
  }

  static void private makeExecutable(File targetPath, String name, String fileExtension) {
    <#assign path = Paths.get(targetPath.absolutePath + File.separator + name + fileExtension)>
    path.toFile().setExecutable(true);
  }

  static void generateBuildScript(File targetPath, ConfigParams config) {
    toFile(targetPath, "build", Scripts.printBuildScript(config), ".sh")
    makeExecutable(targetPath, "build", ".sh")
  }

  static void generateMakeFile(File targetPath, ComponentTypeSymbol comp, File hwcPath, File libraryPath, File[] subPackagesPath, ConfigParams config){
  toFile(targetPath, "CMakeLists", CMake.printTopLevelCMake(targetPath.listFiles(),
    comp,
    targetPath.toPath.toAbsolutePath.relativize(hwcPath.toPath.toAbsolutePath).toString,
    targetPath.toPath.toAbsolutePath.relativize(libraryPath.toPath.toAbsolutePath).toString,
    subPackagesPath, config), ".txt")
  }

  static void generateMakeFileForSubdirs(File targetPath, List<String> subdirectories) {
    <#assign sortedDirs = new ArrayList<String>>
    sortedDirs.addAll(subdirectories)
    sortedDirs.sort(Comparator.naturalOrder())

    toFile(targetPath, "CMakeLists", CMake.printCMakeForSubdirectories(sortedDirs), ".txt")
  }

  static void generateTestMakeFile(File targetPath, ComponentTypeSymbol comp, File hwcPath, File libraryPath, File[] subPackagesPath, ConfigParams config){
    toFile(Paths.get(targetPath.toString(),"test","gtests").toFile(),
      "CMakeLists", CMake.printGoogleTestParameters(comp), ".txt")
    toFile(targetPath, "CMakeLists", CMake.printTopLevelCMake(targetPath.listFiles(),
      comp,
      targetPath.toPath.toAbsolutePath.relativize(hwcPath.toPath.toAbsolutePath).toString,
      targetPath.toPath.toAbsolutePath.relativize(libraryPath.toPath.toAbsolutePath).toString,
      subPackagesPath, config)+CMake.printLinkTestLibraries(comp, subPackagesPath), ".txt")
  }

  static void generateScripts(File targetPath, ComponentTypeSymbol comp, ConfigParams config, List<String> subdirectories) {
    <#assign sortedDirs = new ArrayList<String>>
    sortedDirs.addAll(subdirectories)
    sortedDirs.sort(Comparator.naturalOrder())

    toFile(targetPath, "run", Scripts.printRunScript(comp, config), ".sh")
    toFile(targetPath, "kill", Scripts.printKillScript(sortedDirs), ".sh")
    makeExecutable(targetPath, "run", ".sh")
    makeExecutable(targetPath, "kill", ".sh")
  }

  static void generatePortJson(File targetPath, ComponentTypeSymbol comp, ConfigParams config) {
    if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
      <#assign path = Paths.get(targetPath.absolutePath + File.separator + "ports")>
      toFile(path.toFile, comp.fullName, Comm.printPortJson(comp, config), ".json");
      for (subcomp : comp.subComponents) {
        generatePortJson(targetPath, subcomp, config, comp.fullName)
      }
    }
  }

  static void generatePortJson(File targetPath, ComponentInstanceSymbol comp, ConfigParams config, String prefix) {
    if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
      <#assign path = Paths.get(targetPath.absolutePath + File.separator + "ports")>
      toFile(path.toFile, prefix + "." + comp.name, Comm.printPortJson(comp.type.loadedSymbol, config, prefix + "." + comp.name), ".json");
      for (subcomp : comp.type.loadedSymbol.subComponents) {
        generatePortJson(targetPath, subcomp, config, prefix + "." + comp.name)
      }
    }
  }

    static void generateAdapter(File targetPath, List<String> packageName, String simpleName, ConfigParams config) {
      toFile(targetPath, simpleName + "AdapterTOP", Adapter.generateHeader(packageName, simpleName, config), ".h");
      toFile(targetPath, simpleName + "AdapterTOP", Adapter.generateCpp(packageName, simpleName, config), ".cpp");
    }
}
