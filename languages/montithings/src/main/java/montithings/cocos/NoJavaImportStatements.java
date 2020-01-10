/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.monticore.cd2pojo.Modelfinder;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montithings._symboltable.MontiThingsLanguage;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Checks that there are no java imports used in MontiThings components
 *
 * @author (last commit) Joshua Fürste
 */
public class NoJavaImportStatements implements MontiArcASTComponentCoCo {
  private static final String APPLICATION_MODEL_PATH = "src/main/resources/models";
  private static final String TEST_MODEL_PATH = "src/test/resources/models";

  @Override
  public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
              String.format("0xMT010 ASTComponent node \"%s\" has no " +
                              "symbol. Did you forget to run the " +
                              "SymbolTableCreator before checking cocos?",
                      node.getName()));
      return;
    }
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();

    // Get all import statements
    ArrayList<ImportStatement> importStatements = (ArrayList<ImportStatement>) comp.getImports();

    for (ImportStatement importStatement : importStatements) {
      // Check if import statement is a star import. If no then check if import statement imports a MontiThings component
      if (!importStatement.isStar()) {
        Scope encScope = comp.getEnclosingScope();
        Optional<ComponentSymbol> compsym = encScope.<ComponentSymbol> resolve(importStatement.getStatement(),
            ComponentSymbol.KIND);

        // Check if import statement imports a MontiThings component
        if (!compsym.isPresent()) {
          Log.error("0xMT124 The import statement: " + importStatement.getStatement() +
                  " imports no MontiThings component!");
          return;
        }
      }
    }
  }
}

