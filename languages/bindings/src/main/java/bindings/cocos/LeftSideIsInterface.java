// (c) https://github.com/MontiCore/monticore
package bindings.cocos;

import bindings._ast.ASTBindingRule;
import bindings._cocos.BindingsASTBindingRuleCoCo;
import montithings.generator.cd2cpp.Modelfinder;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.MontiThingsLanguage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Checks that Left Side of Binding is Interface
 */
public class LeftSideIsInterface implements BindingsASTBindingRuleCoCo {
  private static final String APPLICATION_MODEL_PATH = "src/main/resources/models";

  private static final String TEST_MODEL_PATH = "src/test/resources/models";

  public static final String LEFT_SIDE_NO_INTERFACE = "Left side is no interface!";

  public static final String NO_MODEL_INTERFACE = "Interface has no model interface!";

  @Override
  public void check(ASTBindingRule node) {
    // Reads in interface name
    String interfaceName = node.getInterfaceComponent().toString();

    // Models are either under "src/test/resources/models" or "src/main/resources/models"
    // modelSubDirs contains all model files of the test or application
    List<String> foundModels;
    boolean application = true;
    try {
      foundModels = Modelfinder.getModelsInModelPath(Paths.get(APPLICATION_MODEL_PATH).toFile(),
          MontiThingsLanguage.FILE_ENDING);
    }
    catch (Exception e) {
      foundModels = Modelfinder.getModelsInModelPath(Paths.get(TEST_MODEL_PATH).toFile(),
          MontiThingsLanguage.FILE_ENDING);
      application = false;
    }

    // Read interface model line by line and check if its contain the keyword "interface component"
    // Pass Coco if keyword "interface component" is contained in model
    for (String model : foundModels) {
      String qualifiedModelName = Names.getSimpleName(model);
      if (qualifiedModelName.equals(interfaceName)) {
        // Every entry contains 1 line of the model
        ArrayList<String> modelLines = new ArrayList<String>();

        // Append all lines to modelLines
        try {
          String modelPath;
          if (application) {
            modelPath = new File(APPLICATION_MODEL_PATH).getAbsolutePath();
          }
          else {
            modelPath = new File(TEST_MODEL_PATH + "/" + model + ".mt").getAbsolutePath();
          }
          BufferedReader reader = new BufferedReader(new FileReader(modelPath));
          String line = reader.readLine();
          while (line != null) {
            modelLines.add(line);
            line = reader.readLine();
          }
        }
        catch (IOException e) {
          e.printStackTrace();
        }

        // Check if modelLines contains keywords "interface component"
        for (String line : modelLines) {
          if (line.contains("interface") && line.contains("component")) {
            return;
          }
        }

        // Return with error if keyword "interface component" is contained in model
        Log.error(LEFT_SIDE_NO_INTERFACE);
        return;
      }
    }

    // If method reaches this point the interface given in the binding is not found
    Log.error(NO_MODEL_INTERFACE);
  }
}