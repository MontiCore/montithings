// (c) https://github.com/MontiCore/monticore
package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.behavior.AtomicComponentStandardImplementation
import montithings.generator.codegen.xtend.util.CMake
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.codegen.xtend.util.ArduinoReadme
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.TargetPlatform
import de.monticore.ast.ASTCNode
import de.monticore.io.FileReaderWriter
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.List
import java.util.HashMap
import arcbasis._ast.ASTComponentType
import arcbasis._symboltable.ComponentTypeSymbol
import montithings._ast.ASTMTComponentType
import montithings._ast.ASTBehavior

/**
 * Main entry point for generator. From this all target artifacts are generated for a component. 
 * It uses dispatching for calling the right implementation generator.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class MTGenerator {

  def static void generateAll(File targetPath, File hwc, ComponentTypeSymbol comp, List<String> foundModels, String compname,
                         HashMap<String, String> interfaceToImplementation, TargetPlatform platform) {
    Identifier.createInstance(comp)

    toFile(targetPath, compname + "Input", Input.generateInputHeader(comp, compname), ".h");
    toFile(targetPath, compname + "Input", Input.generateImplementationFile(comp, compname), ".cpp");
    toFile(targetPath, compname + "Result", Result.generateResultHeader(comp, compname), ".h");
    toFile(targetPath, compname + "Result", Result.generateImplementationFile(comp, compname), ".cpp");
    toFile(targetPath, compname, ComponentGenerator.generateHeader(comp, compname, interfaceToImplementation, platform), ".h");
    toFile(targetPath, compname, ComponentGenerator.generateImplementationFile(comp, compname), ".cpp");
    

    var boolean existsHWC = ComponentHelper.existsHWCClass(hwc, comp.packageName + "." + compname + "Impl");

    if (!existsHWC && comp.isAtomic) {
		  generateBehaviorImplementation(comp, targetPath, compname)
    }
    
	// Generate inner components
    for(innerComp : comp.innerComponents) {
    	//TODO Fix hwc path for inner components
    	
    	generateAll(targetPath.toPath.resolve(compname + "gen").toFile, hwc, innerComp as ComponentTypeSymbol, foundModels, compname, interfaceToImplementation, platform);
    }
    
	// Generate deploy class
    if (ComponentHelper.isApplication(comp)) {
      if (platform == TargetPlatform.ARDUINO) {
      	var sketchDirectory = new File(targetPath.getParentFile().getPath() + File.separator + "Deploy" + compname);
      	sketchDirectory.mkdir();
        toFile(sketchDirectory, "Deploy" + compname, Deploy.generateDeployArduino(comp, compname),".ino");
        toFile(targetPath.getParentFile(), "README", ArduinoReadme.printArduinoReadme(targetPath.name, compname),".txt");
      } else {
        toFile(targetPath, "Deploy" + compname, Deploy.generateDeploy(comp, compname),".cpp");
      }
    }

  }

  def static generateBehaviorImplementation(ComponentTypeSymbol comp, File targetPath, String compname) {
    var compAST = comp.astNode as ASTMTComponentType
    var boolean hasBehavior = ComponentHelper.hasBehavior(comp);

    if (!hasBehavior) {
      toFile(targetPath, compname + "Impl",
        AtomicComponentStandardImplementation.generateAbstractAtomicImplementationHeader(comp, compname),".h")
      toFile(targetPath, compname + "Impl",
        AtomicComponentStandardImplementation.generateImplementationFile(comp, compname),".cpp")
    }
  }

  def static private toFile(File targetPath, String name, String content, String fileExtension) {
    var Path path = Paths.get(targetPath.absolutePath + File.separator + name + fileExtension)
    println("Writing to file " + path + ".");
    FileReaderWriter.storeInFile(path, content)
  }
	
  def static generateMakeFile(File targetPath, ComponentTypeSymbol comp, File hwcPath, File libraryPath, File[] subPackagesPath, TargetPlatform platform){
	
	toFile(targetPath, "CMakeLists", CMake.printTopLevelCMake(targetPath.listFiles(),
		comp,
		targetPath.toPath.toAbsolutePath.relativize(hwcPath.toPath.toAbsolutePath).toString,
		targetPath.toPath.toAbsolutePath.relativize(libraryPath.toPath.toAbsolutePath).toString,
		subPackagesPath, platform), ".txt")
  }
}
