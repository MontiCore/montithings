// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._symboltable.ComponentInstanceSymbol
import de.monticore.io.FileReaderWriter
import java.io.File
import java.nio.file.Paths
import java.util.ArrayList
import java.util.Comparator
import java.util.List
import montithings.generator.codegen.ConfigParams
import montithings.generator.codegen.xtend.behavior.Implementation
import montithings.generator.codegen.xtend.util.ArduinoReadme
import montithings.generator.codegen.xtend.util.CMake
import montithings.generator.codegen.xtend.util.Comm
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.codegen.xtend.util.Scripts
import montithings.generator.helper.ComponentHelper
import montithings.generator.helper.FileHelper

/**
 * Main entry point for generator. From this all target artifacts are generated for a component. 
 * It uses dispatching for calling the right implementation generator.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class MTGenerator {

  def static void generateAll(File targetPath, File hwc, ComponentTypeSymbol comp, String compname, ConfigParams config, boolean generateDeploy) {
    Identifier.createInstance(comp)

    var useWsPorts = (config.getSplittingMode() != ConfigParams.SplittingMode.OFF && generateDeploy);

    toFile(targetPath, compname + "Input", Input.generateInputHeader(comp, compname, config), ".h");
    toFile(targetPath, compname + "Input", Input.generateImplementationFile(comp, compname, config), ".cpp");
    toFile(targetPath, compname + "Result", Result.generateResultHeader(comp, compname, config), ".h");
    toFile(targetPath, compname + "Result", Result.generateImplementationFile(comp, compname, config), ".cpp");
    toFile(targetPath, compname, ComponentGenerator.generateHeader(comp, compname, config, useWsPorts), ".h");
    toFile(targetPath, compname, ComponentGenerator.generateImplementationFile(comp, compname, config, useWsPorts), ".cpp");
    
    if (comp.isAtomic) {
      var boolean existsHWC = FileHelper.existsHWCClass(hwc, comp.packageName + "." + compname);
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
        var sketchDirectory = new File(targetPath.getParentFile().getPath() + File.separator + "Deploy" + compname);
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

  def static generateBehaviorImplementation(ComponentTypeSymbol comp, File targetPath, String compname, boolean existsHWC) {
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

  def static private toFile(File targetPath, String name, String content, String fileExtension) {
    var path = Paths.get(targetPath.absolutePath + File.separator + name + fileExtension)
    println("Writing to file " + path + ".")
    FileReaderWriter.storeInFile(path, content)
  }

  def static private makeExecutable(File targetPath, String name, String fileExtension) {
    var path = Paths.get(targetPath.absolutePath + File.separator + name + fileExtension)
    path.toFile().setExecutable(true);
  }

  def static generateBuildScript(File targetPath, ConfigParams config) {
    toFile(targetPath, "build", Scripts.printBuildScript(config), ".sh")
    makeExecutable(targetPath, "build", ".sh")
  }
  
  def static generateMakeFile(File targetPath, ComponentTypeSymbol comp, File hwcPath, File libraryPath, File[] subPackagesPath, ConfigParams config){
  toFile(targetPath, "CMakeLists", CMake.printTopLevelCMake(targetPath.listFiles(),
    comp,
    targetPath.toPath.toAbsolutePath.relativize(hwcPath.toPath.toAbsolutePath).toString,
    targetPath.toPath.toAbsolutePath.relativize(libraryPath.toPath.toAbsolutePath).toString,
    subPackagesPath, config), ".txt")
  }

  def static generateMakeFileForSubdirs(File targetPath, List<String> subdirectories) {
    var sortedDirs = new ArrayList<String>
    sortedDirs.addAll(subdirectories)
    sortedDirs.sort(Comparator.naturalOrder())

    toFile(targetPath, "CMakeLists", CMake.printCMakeForSubdirectories(sortedDirs), ".txt")
  }

  def static generateScripts(File targetPath, ComponentTypeSymbol comp, ConfigParams config, List<String> subdirectories) {
    var sortedDirs = new ArrayList<String>
    sortedDirs.addAll(subdirectories)
    sortedDirs.sort(Comparator.naturalOrder())

    toFile(targetPath, "run", Scripts.printRunScript(comp, config), ".sh")
    toFile(targetPath, "kill", Scripts.printKillScript(sortedDirs), ".sh")
    makeExecutable(targetPath, "run", ".sh")
    makeExecutable(targetPath, "kill", ".sh")
  }

  def static generatePortJson(File targetPath, ComponentTypeSymbol comp, ConfigParams config) {
    if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
      var path = Paths.get(targetPath.absolutePath + File.separator + "ports")
      toFile(path.toFile, comp.fullName, Comm.printPortJson(comp, config), ".json");
      for (subcomp : comp.subComponents) {
        generatePortJson(targetPath, subcomp, config, comp.fullName)
      }
    }
  }

  def static void generatePortJson(File targetPath, ComponentInstanceSymbol comp, ConfigParams config, String prefix) {
    if (config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL) {
      var path = Paths.get(targetPath.absolutePath + File.separator + "ports")
      toFile(path.toFile, prefix + "." + comp.name, Comm.printPortJson(comp.type.loadedSymbol, config, prefix + "." + comp.name), ".json");
      for (subcomp : comp.type.loadedSymbol.subComponents) {
        generatePortJson(targetPath, subcomp, config, prefix + "." + comp.name)
      }
    }
  }

    def static void generateAdapter(File targetPath, List<String> packageName, String simpleName, ConfigParams config) {
      toFile(targetPath, simpleName + "AdapterTOP", Adapter.generateHeader(packageName, simpleName, config), ".h");
      toFile(targetPath, simpleName + "AdapterTOP", Adapter.generateCpp(packageName, simpleName, config), ".cpp");
    }
}
