// (c) https://github.com/MontiCore/monticore
package bindings.cocos;

import bindings._ast.ASTBindingsCompilationUnit;
import bindings._cocos.BindingsCoCoChecker;

/**
 * Wrapper for test input data
 *
 * @author (last commit) kirchhof
 * @version 1.0, 01.03.2019
 * @since 1.0
 */
public class CocoTestInput {

    /** The AST to execute the test on */
    private ASTBindingsCompilationUnit ast;

    /** The checker that executes the CoCo (i.e. the system under test) */
    private BindingsCoCoChecker checker;

    /* ============================================================ */
    /* ====================== GENERATED CODE ====================== */
    /* ============================================================ */

    public CocoTestInput(ASTBindingsCompilationUnit ast, BindingsCoCoChecker checker) {
        this.ast = ast;
        this.checker = checker;
    }

    public ASTBindingsCompilationUnit getAst() {
        return ast;
    }

    public BindingsCoCoChecker getChecker() {
        return checker;
    }
}
