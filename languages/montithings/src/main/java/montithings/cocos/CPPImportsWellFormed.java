/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import montithings._ast.ASTCPPImportStatementLOCAL;

/**
 * Checks that the CPP Imports are well-formed.
 * Mostly a technical limitation due to the way that
 * '"' isn't parsed correctly.
 *
 * @author (last commit) Joshua Fürste
 */
class CPPImportsWellFormed extends ASTCPPImportStatementLOCAL {
}
