// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import de.monticore.io.FileReaderWriter
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.List
import montithings.generator.codegen.ConfigParams
import montithings.generator.codegen.xtend.behavior.Implementation
import montithings.generator.codegen.xtend.util.ArduinoReadme
import montithings.generator.codegen.xtend.util.CMake
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.helper.ComponentHelper

/**
 * Main entry point for generator. From this all target artifacts are generated for a component. 
 * It uses dispatching for calling the right implementation generator.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class MTGenerator {

  def static void generateAll(File targetPath, File hwc, ComponentTypeSymbol comp, String compname, ConfigParams config) {
    Identifier.createInstance(comp)

    toFile(targetPath, compname + "Input", Input.generateInputHeader(comp, compname, config), ".h");
    toFile(targetPath, compname + "Input", Input.generateImplementationFile(comp, compname, config), ".cpp");
    toFile(targetPath, compname + "Result", Result.generateResultHeader(comp, compname, config), ".h");
    toFile(targetPath, compname + "Result", Result.generateImplementationFile(comp, compname, config), ".cpp");
    toFile(targetPath, compname, ComponentGenerator.generateHeader(comp, compname, config), ".h");
    toFile(targetPath, compname, ComponentGenerator.generateImplementationFile(comp, compname, config), ".cpp");
    
    if (comp.isAtomic) {
      var boolean existsHWC = ComponentHelper.existsHWCClass(hwc, comp.packageName + "." + compname + "Impl");
		  generateBehaviorImplementation(comp, targetPath, compname, existsHWC)
    }
    
	// Generate inner components
    for(innerComp : comp.innerComponents) {
    	//TODO Fix hwc path for inner components
    	
    	generateAll(targetPath.toPath.resolve(compname + "gen").toFile, hwc, innerComp, compname, config);
    }
    
	// Generate deploy class
    if (ComponentHelper.isApplication(comp)) {
      if (config.getTargetPlatform() == ConfigParams.TargetPlatform.ARDUINO) {
      	var sketchDirectory = new File(targetPath.getParentFile().getPath() + File.separator + "Deploy" + compname);
      	sketchDirectory.mkdir();
        toFile(sketchDirectory, "Deploy" + compname, Deploy.generateDeployArduino(comp, compname),".ino");
        toFile(targetPath.getParentFile(), "README", ArduinoReadme.printArduinoReadme(targetPath.name, compname),".txt");
      } else {
        toFile(targetPath, "Deploy" + compname, Deploy.generateDeploy(comp, compname),".cpp");
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
    var Path path = Paths.get(targetPath.absolutePath + File.separator + name + fileExtension)
    println("Writing to file " + path + ".");
    FileReaderWriter.storeInFile(path, content)
  }
	
  def static generateMakeFile(File targetPath, ComponentTypeSymbol comp, File hwcPath, File libraryPath, File[] subPackagesPath, ConfigParams config){
	
	toFile(targetPath, "CMakeLists", CMake.printTopLevelCMake(targetPath.listFiles(),
		comp,
		targetPath.toPath.toAbsolutePath.relativize(hwcPath.toPath.toAbsolutePath).toString,
		targetPath.toPath.toAbsolutePath.relativize(libraryPath.toPath.toAbsolutePath).toString,
		subPackagesPath, config), ".txt")
  }
}
