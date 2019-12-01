package bindings.cocos;

import de.monticore.ModelingLanguage;
import de.monticore.cd2pojo.Modelfinder;
import de.se_rwth.commons.Names;
import bindings._ast.ASTElement;
import de.se_rwth.commons.logging.Log;
import bindings._ast.ASTBindingRule;
import bindings._cocos.BindingsASTBindingRuleCoCo;
import bindings._symboltable.BindingsLanguage;
import montithings._symboltable.MontiThingsLanguage;
import groovyjarjarantlr.collections.AST;

import java.nio.file.Paths;
import java.util.*;

/**
 * Checks that Implementation component exists
 */
public class ImplementationExists implements BindingsASTBindingRuleCoCo {
    private static final String APPLICATION_MODEL_PATH = "src/main/resources/models";
    private static final String TEST_MODEL_PATH = "src/test/resources/models";
    public static final String NO_MODEL_IMPLEMENTATION = "Implementation has no model implementation!";

    @Override
    public void check (ASTBindingRule node) {
        String implementationName = node.getImplementationComponent().toString();

        // Models are either under "src/test/resources/models" or "src/main/resources/models"
        List<String> foundModels;
        try {
            foundModels = Modelfinder.getModelsInModelPath(Paths.get(APPLICATION_MODEL_PATH).toFile(),
                    MontiThingsLanguage.FILE_ENDING);
        } catch (Exception e) {
            foundModels = Modelfinder.getModelsInModelPath(Paths.get(TEST_MODEL_PATH).toFile(),
                    MontiThingsLanguage.FILE_ENDING);
        }

        // compare implementationName with foundModels names and check if implementation model exists
        for (String model : foundModels) {
            String qualifiedModelName = Names.getSimpleName(model);
            if (qualifiedModelName.equals(implementationName)) {
                // return without failure when model with implementation name exists
                return;
            }
        }
        Log.error(NO_MODEL_IMPLEMENTATION);
    }
}