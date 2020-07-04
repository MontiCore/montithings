// (c) https://github.com/MontiCore/monticore
package montithings.generator;

import arcbasis._ast.ASTMontiArcNode;
import bindings.BindingsTool;
import bindings.CocoInput;
import bindings._symboltable.BindingsLanguage;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
import montithings.MontiThingsTool;
import montithings._symboltable.ComponentTypeSymbol;
import montithings._symboltable.MontiThingsLanguage;
import montithings._symboltable.ResourcePortSymbol;
import montithings.generator.cd2cpp.CppGenerator;
import montithings.generator.cd2cpp.Modelfinder;
import montithings.generator.codegen.TargetPlatform;
import montithings.generator.codegen.xtend.MTGenerator;
import montithings.generator.helper.ComponentHelper;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.EMPTY_LIST;

/**
 * TODO
 *
 * @authors (last commit) JFuerste, Daniel von Mirbach
 */
public class MontiThingsGeneratorTool extends MontiThingsTool {

  private static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";

  public void generate(File modelPath, File target, File hwcPath, TargetPlatform platform) {

    /* ============================================================ */
    /* ==================== Copy HWC to target ==================== */
    /* ============================================================ */
    try {
      if (platform == TargetPlatform.ARDUINO) {
        FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath()).toFile());
      } else {
        FileUtils.copyDirectory(hwcPath, Paths.get(target.getAbsolutePath(), "hwc").toFile());
      }
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
    /* ======================= Check Models ======================= */
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
      ComponentTypeSymbol comp = symTab.<ComponentTypeSymbol>resolve(qualifiedModelName,
          ComponentTypeSymbol.KIND).get();

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

      /* ============================================================ */
      /* ==================== Generate Components =================== */
      /* ============================================================ */

      String compname = comp.getName();

      // Check if component is interface
      if (interfaceToImplementation.containsKey(comp.getName())) {
        compname = interfaceToImplementation.get(comp.getName());
      }

      // Generate Files
      MTGenerator.generateAll(
          Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName()))
              .toFile(), hwcPath, comp, foundModels, compname, interfaceToImplementation, platform);

      for (ResourcePortSymbol resourcePortSymbol : ComponentHelper
          .getResourcePortsInComponent(comp)) {
        if (resourcePortSymbol.isIpc()) {
          // Generate necessary headers for application
          File headerPath = Paths.get(target.getAbsolutePath(),
              Names.getPathFromPackage(comp.getPackageName()),
              comp.getName() + "-" + StringTransformations.capitalize(resourcePortSymbol.getName()))
              .toFile();
          headerPath.mkdir();
          File libraryPath = Paths.get("target/generated-sources/montithings-RTE").toFile();
          MTGenerator.generateIPCServer(headerPath, resourcePortSymbol, comp, libraryPath, hwcPath, platform, true);

          // Generate IPC Port implementation
          File implPath = Paths.get(target.getAbsolutePath(),
              "resource-ports",
              Names.getPathFromPackage(comp.getPackageName()),
              comp.getName() + "-" + StringTransformations.capitalize(resourcePortSymbol.getName()))
              .toFile();
          implPath.mkdir();
          MTGenerator.generateIPCServer(implPath, resourcePortSymbol, comp, libraryPath, hwcPath, platform, false);

          // Copy IPC Port HWC to target
          boolean existsHwc = ComponentHelper.existsIPCServerHWCClass(hwcPath, comp, resourcePortSymbol.getName());
          if (existsHwc) {
            try {
              // Copy HWC files
              String resourcePortName = comp.getName() + "-" +
                  StringTransformations.capitalize(resourcePortSymbol.getName());
              File hwcDirectory = Paths.get(hwcPath.getAbsolutePath(),
                  Names.getPathFromPackage(comp.getPackageName()),
                  resourcePortName).toFile();
              FileUtils.copyDirectory(hwcDirectory, implPath);

              // Remove HWC files from components' HWC
              File componentResourcePortImplPath = Paths.get(target.getAbsolutePath(), "hwc",
                  Names.getPathFromPackage(comp.getPackageName()),
                  comp.getName() + "-" + StringTransformations.capitalize(resourcePortSymbol.getName()))
                  .toFile();
              FileUtils.deleteDirectory(componentResourcePortImplPath);
            }
            catch (IOException e) {
              System.err.println(e.getMessage());
              e.printStackTrace();
            }
          }
        }
      }
    }

    /* ============================================================ */
    /* ====================== CMakeLists.txt ====================== */
    /* ============================================================ */

    for (String model : foundModels) {
      String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
      montithings._symboltable.ComponentTypeSymbol comp = symTab.<montithings._symboltable.ComponentTypeSymbol>resolve(qualifiedModelName,
          montithings._symboltable.ComponentTypeSymbol.KIND).get();

      if (comp.isApplication()) {
        File libraryPath = Paths.get(target.getAbsolutePath(), "montithings-RTE").toFile();
        // Check for Subpackages
        File[] subPackagesPath = getSubPackagesPath(modelPath.getAbsolutePath());

        // 6 generate make file
        if (platform != TargetPlatform.ARDUINO) { // Arduino uses its own build system
          Log.info("Generate CMake file", "MontiThingsGeneratorTool");
          MTGenerator.generateMakeFile(target, comp, hwcPath, libraryPath,
              subPackagesPath, platform);
        }
      }
    }

    /* ============================================================ */
    /* ====================== Class Diagrams ====================== */
    /* ============================================================ */

    generateCD(modelPath, target);
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
        Log.error("0x???? File separator should be a single char. Use a less strange system");
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

  private void generateCD(File modelPath, File targetFilepath) {
    List<String> foundModels = Modelfinder.getModelsInModelPath(modelPath, CD4AnalysisLanguage.FILE_ENDING);
    for (String model : foundModels) {
      String simpleName = Names.getSimpleName(model);
      String packageName = Names.getQualifier(model);

      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new CppGenerator(outDir, Paths.get(modelPath.getAbsolutePath()), model,
          Names.getQualifiedName(packageName, simpleName)).generate();
    }
  }

}
