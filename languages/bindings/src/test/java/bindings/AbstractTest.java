// (c) https://github.com/MontiCore/monticore
package bindings;

import de.se_rwth.commons.logging.Log;
import bindings._ast.ASTBindingsCompilationUnit;
import bindings._parser.BindingsParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Helpers
 *
 * @author (last commit) kirchhof
 * @version 1.0, 28.02.2019
 * @since 1.0
 */
public class AbstractTest {

    @BeforeAll
    static void initialize() {
        Log.enableFailQuick(false);
    }

    @BeforeEach
    void setUp() {
        Log.getFindings().clear();
    }

    /**
     * Parses a model and ensures that the root node is present.
     *
     * @param modelFile the full file name of the model.
     * @return the root of the parsed model.
     */
    protected ASTBindingsCompilationUnit parseModel(String modelFile) {
        Path model = Paths.get(modelFile);
        BindingsParser parser = new BindingsParser();
        Optional<ASTBindingsCompilationUnit> optAutomaton;
        try {
            optAutomaton = parser.parse(model.toString());
            assertFalse(parser.hasErrors());
            assertTrue(optAutomaton.isPresent());
            return optAutomaton.get();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("There was an exception when parsing the model " + modelFile + ": "
                    + e.getMessage());
        }
        return null;
    }
}
