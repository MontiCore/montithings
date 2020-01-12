// (c) https://github.com/MontiCore/monticore
package bindings;

import bindings._ast.ASTBindingsCompilationUnit;
import bindings._cocos.BindingsCoCoChecker;
import bindings._parser.BindingsParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BindingsTool {
    
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

    public CocoInput prepareTest(String pathToModelFile) {
        // prepare input
        ASTBindingsCompilationUnit ast = parseModel(pathToModelFile);
        BindingsCoCoChecker checker = new BindingsCoCoChecker();

        // bundle input
        return new CocoInput(ast, checker);
    }

    public void executeCoCo(CocoInput input) {
        input.getChecker().checkAll(input.getAst());
    }

    public void checkResults(String expectedError) {
        checkResults(Collections.singletonList(expectedError));
    }

    public void checkResults(Collection<String> expectedErrors) {
        Collection<Finding> findings = Log.getFindings();
        List<Finding> expectedFindings = expectedErrors.stream()
                .map(Finding::error)
                .collect(Collectors.toList());
        assertThat(findings).containsExactlyElementsOf(expectedFindings);
    }
}
