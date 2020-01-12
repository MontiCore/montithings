// (c) https://github.com/MontiCore/monticore
package montithings.generator;

import bindings._ast.ASTBindingsNode;
import de.monticore.ast.ASTNode;
import montiarc._symboltable.PortSymbol;
import montithings.generator.codegen.xtend.MTGenerator;
import montithings.generator.helper.ComponentHelper;
import de.monticore.cd2pojo.Modelfinder;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMontiArcNode;
import montiarc._symboltable.ComponentSymbol;
import montithings.MontiThingsTool;
import montithings._symboltable.MontiThingsLanguage;
import montithings._symboltable.ResourcePortSymbol;
import bindings._symboltable.BindingsLanguage;
import bindings._cocos.BindingsCoCoChecker;
import bindings.*;
import org.apache.commons.io.FileUtils;

import javax.sound.sampled.Port;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.*;

/**
 * TODO
 *
 * @authors (last commit) JFuerste, Daniel von Mirbach
 */
public class MontiThingsGeneratorTool extends MontiThingsTool {

  private static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";

  public void generate(File modelPath, File target, File hwcPath) {

    /* ============================================================ */
    /* ==================== Copy HWC to target ==================== */
    /* ============================================================ */
    try {
      FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath(), "hwc").toFile());
    }
    catch (IOException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
    }

    /* ============================================================ */
    /* ====================== Check Bindings ====================== */
    /* ============================================================ */
    // 1. Get Bindings
    HashMap<String, String> interfaceToImplementation = getInterfaceImplementationMatching(
        hwcPath.getAbsolutePath());
    List<String> foundBindings = Modelfinder
        .getModelsInModelPath(hwcPath, BindingsLanguage.FILE_ENDING);
    Log.info("Initializing Bindings: ", "MontiArcGeneratorTool");

    // 2. Parse and check Cocos of bindings
    for (String binding : foundBindings) {
      Log.info("Check Binding: " + binding, "MontiArcGeneratorTool");
      String qualifiedModelName =
          hwcPath.getAbsolutePath() + "/" + Names.getQualifier(binding) + "/" +
              Names.getSimpleName(binding) + ".mtb";
      BindingsTool bindingsTool = new BindingsTool();
      CocoInput input = bindingsTool.prepareTest(qualifiedModelName);
      bindingsTool.executeCoCo(input);
      bindingsTool.checkResults(EMPTY_LIST);
    }

    /* ============================================================ */
    /* ======================? Check Models ======================= */
    /* ============================================================ */
    // 1. Find all .mt files
    List<String> foundModels = Modelfinder
        .getModelsInModelPath(modelPath, MontiThingsLanguage.FILE_ENDING);
    // 2. Initialize SymbolTable
    Log.info("Initializing symboltable", "MontiArcGeneratorTool");
    String basedir = getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(),
        target.getAbsolutePath());
    Scope symTab = initSymbolTable(modelPath, Paths.get(basedir + LIBRARY_MODELS_FOLDER).toFile(),
        hwcPath);

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);

      // 3. parse + resolve model
      Log.info("Parsing model:" + qualifiedModelName, "MontiThingsGeneratorTool");
      ComponentSymbol comp = symTab.<ComponentSymbol>resolve(qualifiedModelName,
          ComponentSymbol.KIND).get();

      // 4. check cocos
      Log.info("Check model: " + qualifiedModelName, "MontiThingsGeneratorTool");
      checkCoCos((ASTMontiArcNode) comp.getAstNode().get());

      // 5. generate
      Log.info("Generate model: " + qualifiedModelName, "MontiThingsGeneratorTool");

      // check if component is implementation
      if (interfaceToImplementation.containsValue(comp.getName())) {
        // Dont generate files for implementation. They are generated when interface is there
        continue;
      }

      String compname = comp.getName();

      // Check if component is interface
      if (interfaceToImplementation.containsKey(comp.getName())) {
        compname = interfaceToImplementation.get(comp.getName());
      }

      // Generate Files
      MTGenerator.generateAll(
          Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName()))
              .toFile(),
          hwcPath, comp, foundModels, compname, interfaceToImplementation);

      for (ResourcePortSymbol resourcePortSymbol : ComponentHelper
          .getResourcePortsInComponent(comp)) {
        if (resourcePortSymbol.isIpc()) {
          File path = Paths.get(target.getAbsolutePath(),
              Names.getPathFromPackage(comp.getPackageName()),
              comp.getName() + "-"
                  + StringTransformations.capitalize(resourcePortSymbol.getName()))
              .toFile();
          path.mkdir();
          File libraryPath = Paths.get("target/montithings-RTE").toFile();
          MTGenerator.generateIPCServer(path, resourcePortSymbol, comp, libraryPath, hwcPath);
        }
      }
    }

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
      ComponentSymbol comp = symTab.<ComponentSymbol>resolve(qualifiedModelName,
          ComponentSymbol.KIND).get();

      if (comp.getStereotype().containsKey("deploy")) {
        File libraryPath = Paths.get(target.getAbsolutePath(), "montithings-RTE").toFile();
        // 5 generate libs
				/*try {
					FileUtils.copyDirectoryToDirectory(Paths.get("src/main/resources/rte/montithings-RTE").toFile(), target);
				} catch (IOException e) {
					e.printStackTrace();
				}*/

        // Check for Subpackages
        File[] subPackagesPath = getSubPackagesPath(modelPath.getAbsolutePath());

        // 6 generate make file
        Log.info("Generate CMake file", "MontiThingsGeneratorTool");
        MTGenerator.generateMakeFile(target, comp, hwcPath, libraryPath,
            subPackagesPath);
      }
    }

  }

  /**
   * Returns list of all subpackages paths
   *
   * @param modelPath
   * @return
   */
  private File[] getSubPackagesPath(String modelPath) {
    ArrayList<File> subPackagesPaths = new ArrayList<>();
    File[] subDirs = new File(modelPath).listFiles(File::isDirectory);

    // Iterate over subdirectories of the model folder and add the paths of the subdirs to array
    for (File subDir : subDirs) {
      subPackagesPaths.add(new File(subDir.getAbsolutePath()));
    }

    // cast to ArrayList to File[] and return
    return subPackagesPaths.toArray(new File[subPackagesPaths.size()]);
  }

  /**
   * Returns InterfaceComponent name matching to Implementation name as HashMap
   *
   * @param hwcPath
   * @return
   */
  private HashMap<String, String> getInterfaceImplementationMatching(String hwcPath) {
    // Every entry contains matching from interface to implementation (interface -> implementation)
    HashMap<String, String> interfaceToImplementation = new HashMap<String, String>();

    // 1. Check if binding exists
    File[] hwcSubDirs = new File(hwcPath).listFiles(File::isDirectory);
    for (File subdir : hwcSubDirs) {
      if (new File(subdir.getAbsolutePath(), "bindings.mtb").exists()) {
        // Every entry contains 1 binding
        ArrayList<String> bindingList = new ArrayList<String>();

        // 2. Append all lines to bindingList
        try {
          String bindingsPath = subdir.getAbsolutePath() + "/bindings.mtb";
          BufferedReader reader = new BufferedReader(new FileReader(bindingsPath));
          String line = reader.readLine();
          while (line != null) {
            bindingList.add(line);
            line = reader.readLine();
          }
        }
        catch (IOException e) {
          e.printStackTrace();
        }

        // 3. Append interface to implementation matching to map
        for (String binding : bindingList) {
          String interfaceComponent = binding.split("->")[0].replace(" ", "");
          String implementationComponent = binding.split(" -> ")[1].replace(";", "")
              .replace(" ", "");
          interfaceToImplementation.put(interfaceComponent, implementationComponent);
        }
      }
    }

    return interfaceToImplementation;
  }

  /**
   * Compares the two paths and returns the common path. The common path is the
   * basedir.
   *
   * @param modelPath
   * @param targetPath
   * @return
   */
  private String getBasedirFromModelAndTargetPath(String modelPath, String targetPath) {
    String basedir = "";

    StringBuilder sb = new StringBuilder();
    String seperator = File.separator;
    int lastFolderIndex = 0;
    for (int i = 0; i < modelPath.length(); i++) {
      // Assuming a seperator is always length 1
      if (seperator.length() != 1) {
        Log.error("0x???? File seperator should be a single char. Use a less strange system");
      }
      else if (modelPath.charAt(i) == seperator.charAt(0)) {
        lastFolderIndex = i;
      }

      if (modelPath.charAt(i) == targetPath.charAt(i)) {
        sb.append(modelPath.charAt(i));
      }
      else {
        // basedir includes the seperator
        basedir = sb.substring(0, lastFolderIndex + 1);
        break;
      }
    }
    return basedir;
  }

}
