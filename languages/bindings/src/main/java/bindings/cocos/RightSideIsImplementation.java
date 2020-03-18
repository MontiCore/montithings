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
 * Checks that Right Side of Binding is Implementation
 */
public class RightSideIsImplementation implements BindingsASTBindingRuleCoCo {
  private static final String APPLICATION_MODEL_PATH = "src/main/resources/models";

  private static final String TEST_MODEL_PATH = "src/test/resources/models";

  public static final String RIGHT_SIDE_NO_IMPLEMENTATION = "Right side is no implementation!";

  public static final String NO_MODEL_IMPLEMENTATION = "Implementation has no model implementation!";

  // Input: BindingRule with form "Interface -> Implementation;"
  @Override
  public void check(ASTBindingRule node) {
    // Reads in implementation name
    String implementationName = node.getImplementationComponent().toString();

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

    // Read implementation model line by line and check if its contain the keyword "interface component"
    // Pass Coco if keyword "interface component" is not contained in model
    for (String model : foundModels) {
      String qualifiedModelName = Names.getSimpleName(model);
      if (qualifiedModelName.equals(implementationName)) {
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
            Log.error(RIGHT_SIDE_NO_IMPLEMENTATION);
          }
        }

        // Return with no error if keyword "interface component" is not contained in model
        return;
      }
    }

    // If method reaches this point the implementation given in the binding is not found
    Log.error(NO_MODEL_IMPLEMENTATION);
  }
}