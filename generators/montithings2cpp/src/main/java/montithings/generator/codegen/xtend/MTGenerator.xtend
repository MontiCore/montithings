package montithings.generator.codegen.xtend

import montithings.generator.codegen.xtend.behavior.AbstractAtomicImplementation
import montithings.generator.codegen.xtend.util.CMake
import montithings.generator.codegen.xtend.util.Identifier
import montithings.generator.helper.ComponentHelper
import de.monticore.ast.ASTCNode
import de.monticore.io.FileReaderWriter
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.List
import java.util.HashMap
import montiarc._ast.ASTAutomatonBehavior
import montiarc._ast.ASTBehaviorElement
import montiarc._ast.ASTComponent
import montiarc._ast.ASTJavaPBehavior
import montiarc._symboltable.ComponentSymbol
import montithings._symboltable.ResourcePortSymbol
import montithings.generator.codegen.xtend.util.Utils
import montithings._ast.ASTExecutionBlock

/**
 * Main entry point for generator. From this all target artifacts are generated for a component. 
 * It uses dispatching for calling the right implementation generator.
 * 
 * @author  Pfeiffer
 * @version $Revision$,
 *          $Date$
 */
class MTGenerator {

  def static generateAll(File targetPath, File hwc, ComponentSymbol comp, List<String> foundModels, String compname,
                         HashMap<String, String> interfaceToImplementation) {
    Identifier.createInstance(comp)

    toFile(targetPath, compname + "Input", Input.generateInputHeader(comp, compname), ".h");
    toFile(targetPath, compname + "Input", Input.generateInputBody(comp, compname), ".cpp");
    toFile(targetPath, compname + "Result", Result.generateResultHeader(comp, compname), ".h");
    toFile(targetPath, compname + "Result", Result.generateResultBody(comp, compname), ".cpp");
    toFile(targetPath, compname, ComponentGenerator.generateHeader(comp, compname, interfaceToImplementation), ".h");
    toFile(targetPath, compname, ComponentGenerator.generateBody(comp, compname), ".cpp");
    

    var boolean existsHWC = ComponentHelper.existsHWCClass(hwc, comp.packageName + "." + compname + "Impl");

    if (!existsHWC && comp.isAtomic) {
		generateBehaviorImplementation(comp, targetPath, compname)
    }
    
	// Generate inner components
    for(innerComp : comp.innerComponents) {
    	//TODO Fix hwc path for inner components
    	generateAll(targetPath.toPath.resolve(compname + "gen").toFile, hwc, innerComp, foundModels, compname, interfaceToImplementation);
    }
    
	// Generate deploy class
    if (comp.getStereotype().containsKey("deploy")) {
      toFile(targetPath, "Deploy" + compname, Deploy.generateDeploy(comp, compname),".cpp");
    }

  }

  def static generateBehaviorImplementation(ComponentSymbol comp, File targetPath, String compname) {
    var compAST = comp.astNode.get as ASTComponent
    var boolean hasBehavior = false
    for (element : compAST.body.elementList) {
      if (element instanceof ASTBehaviorElement) {
        hasBehavior = true;
        return generateBehavior(element as ASTCNode, comp, targetPath, compname)
      }
    }

    if (!hasBehavior) {
      toFile(targetPath, compname + "Impl",
      	AbstractAtomicImplementation.generateAbstractAtomicImplementationHeader(comp, compname),".h")
      toFile(targetPath, compname + "Impl",
      	AbstractAtomicImplementation.generateAbstractAtomicImplementationBody(comp, compname),".cpp")
    }

  }

  def static private toFile(File targetPath, String name, String content, String fileExtension) {
    var Path path = Paths.get(targetPath.absolutePath + File.separator + name + fileExtension)
    var FileReaderWriter writer = new FileReaderWriter()
    println("Writing to file " + path + ".");
    writer.storeInFile(path, content)
  }

  def private static dispatch generateBehavior(ASTJavaPBehavior ajava, ComponentSymbol comp, File targetPath, String compname) {
    return ""
  }

  def private static dispatch generateBehavior(ASTAutomatonBehavior automaton, ComponentSymbol comp, File targetPath, String compname) {
  	return ""
  }
  
  def private static dispatch generateBehavior(ASTExecutionBlock execBlock, ComponentSymbol comp, File targetPath, String compname) {
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
