package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.behavior.AbstractAtomicImplementation
import montithings.generator.codegen.xtend.util.CMake
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.codegen.xtend.util.libs.CPPLibraries
import montithings.generator.helper.ComponentHelper
import de.monticore.ast.ASTCNode
import de.monticore.io.FileReaderWriter
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.List
import montiarc._ast.ASTAutomatonBehavior
import montiarc._ast.ASTBehaviorElement
import montiarc._ast.ASTComponent
import montiarc._ast.ASTJavaPBehavior
import montiarc._symboltable.ComponentSymbol
import montithings.generator.codegen.xtend.behavior.AutomatonGenerator
import montithings._symboltable.ResourcePortSymbol
import montithings.generator.codegen.xtend.util.Utils

/**
 * Main entry point for generator. From this all target artifacts are generated for a component. 
 * It uses dispatching for calling the right implementation generator.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class MTGenerator {

  def static generateAll(File targetPath, File hwc, ComponentSymbol comp, List<String> foundModels) {
    Identifier.createInstance(comp)

    toFile(targetPath, comp.name + "Input", Input.generateInputHeader(comp), ".h");
    toFile(targetPath, comp.name + "Input", Input.generateInputBody(comp), ".cpp");
    toFile(targetPath, comp.name + "Result", Result.generateResultHeader(comp), ".h");
    toFile(targetPath, comp.name + "Result", Result.generateResultBody(comp), ".cpp");
    toFile(targetPath, comp.name, ComponentGenerator.generateHeader(comp), ".h");
    toFile(targetPath, comp.name, ComponentGenerator.generateBody(comp), ".cpp");
    

    var boolean existsHWC = ComponentHelper.existsHWCClass(hwc, comp.packageName + "." + comp.name + "Impl");

    if (!existsHWC && comp.isAtomic) {
		generateBehaviorImplementation(comp, targetPath)
    }
    
	// Generate inner components
    for(innerComp : comp.innerComponents) {
    	//TODO Fix hwc path for inner components
    	generateAll(targetPath.toPath.resolve(comp.name + "gen").toFile, hwc, innerComp, foundModels);
    }
    
	// Generate deploy class
    if (comp.getStereotype().containsKey("deploy")) {
      toFile(targetPath, "Deploy" + comp.name, Deploy.generateDeploy(comp),".cpp");
    }

  }

  def static generateBehaviorImplementation(ComponentSymbol comp, File targetPath) {
    var compAST = comp.astNode.get as ASTComponent
    var boolean hasBehavior = false
    for (element : compAST.body.elementList) {
      if (element instanceof ASTBehaviorElement) {
        hasBehavior = true;
        return generateBehavior(element as ASTCNode, comp, targetPath)
      }
    }

    if (!hasBehavior) {
      toFile(targetPath, comp.name + "Impl",
      	AbstractAtomicImplementation.generateAbstractAtomicImplementationHeader(comp),".h")
      toFile(targetPath, comp.name + "Impl",
      	AbstractAtomicImplementation.generateAbstractAtomicImplementationBody(comp),".cpp")
    }

  }

  def static private toFile(File targetPath, String name, String content, String fileExtension) {
    var Path path = Paths.get(targetPath.absolutePath + File.separator + name + fileExtension)
    var FileReaderWriter writer = new FileReaderWriter()
    println("Writing to file " + path + ".");
    writer.storeInFile(path, content)
  }

  def private static dispatch generateBehavior(ASTJavaPBehavior ajava, ComponentSymbol comp, File targetPath) {
    return ""
  }

  def private static dispatch generateBehavior(ASTAutomatonBehavior automaton, ComponentSymbol comp, File targetPath) {
  	return ""
  }
	
  def static generateMakeFile(File targetPath, ComponentSymbol comp, File hwcPath, File libraryPath){
	
	toFile(targetPath, "CMakeLists", CMake.printCMake(targetPath.listFiles(),
		comp,
		targetPath.toPath.toAbsolutePath.relativize(hwcPath.toPath.toAbsolutePath).toString,
		targetPath.toPath.toAbsolutePath.relativize(libraryPath.toPath.toAbsolutePath).toString), ".txt")
  }

  def static generateIPCServer(File targetPath, ResourcePortSymbol port, ComponentSymbol comp, File libraryPath, File hwcPath){
  	var existsHWC = ComponentHelper.existsIPCServerHWCClass(hwcPath, comp, port.name)
  	var ipcPath = ComponentHelper.getIPCHWCPath(port, comp, hwcPath);
	toFile(targetPath, port.name.toFirstUpper() + "Server", Utils.printIPCServerHeader(port, comp), ".h")
	toFile(targetPath, port.name.toFirstUpper() + "Server", Utils.printIPCServerBody(port, comp, existsHWC), ".cpp")
	toFile(targetPath, "CMakeLists", CMake.printIPCServerCMake(
		port,
		targetPath.toPath.toAbsolutePath.relativize(libraryPath.toPath.toAbsolutePath).toString,
		targetPath.toPath.toAbsolutePath.relativize(ipcPath.toPath.toAbsolutePath).toString,
		existsHWC
		),
		 ".txt")
  }

	

}
