/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package bindings.cocos;

import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import bindings.AbstractTest;
import bindings._ast.ASTBindingsCompilationUnit;
import bindings._cocos.BindingsCoCoChecker;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Utils for tests
 *
 * @author (last commit) kirchhof
 * @version 1.0, 01.03.2019
 * @since 1.0
 */
public class AbstractCocoTest extends AbstractTest {

    /**
     * Prepare the inputs needed for executing a coco
     * @param pathToModelFile file path to a .tab file
     * @return An AST and a coco checker
     */
    public CocoTestInput prepareTest(String pathToModelFile) {
        // prepare input
        ASTBindingsCompilationUnit ast = parseModel(pathToModelFile);
        BindingsCoCoChecker checker = new BindingsCoCoChecker();

        // bundle input
        return new CocoTestInput(ast, checker);
    }

    public void executeCoCo(CocoTestInput input) {
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
