/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.monticore.cd2pojo.Modelfinder;
import de.monticore.symboltable.ImportStatement;
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
        // Check if import statement imports a MontiThings component
        if (!modelExists(importStatement.getStatement())) {
          Log.error("0xMT124 The import statement: " + importStatement.getStatement() +
                  " imports no MontiThings component!");
          return;
        }
      }
    }
  }

  /**
   * Parses import string and checks if model exists in path given by import statement
   * @param importStatement
   * @return
   */
  private boolean modelExists(String importStatement) {
    // 1. Remove '.' from path
    String[] path = importStatement.split("\\.");

    // 2. Get models in specified path
    List<String> foundModels;
    try {
      foundModels = Modelfinder.getModelsInModelPath(Paths.get(APPLICATION_MODEL_PATH).toFile(),
              MontiThingsLanguage.FILE_ENDING);
    } catch (Exception e) {
      foundModels = Modelfinder.getModelsInModelPath(Paths.get(TEST_MODEL_PATH).toFile(),
              MontiThingsLanguage.FILE_ENDING);
    }

    // 4. Check if model exists
    String compName = path[path.length - 1];
    for (String model : foundModels) {
      String qualifiedModelName = Names.getSimpleName(model);
      if (qualifiedModelName.equals(compName)) {
        // return without failure when model with implementation name exists
        return true;
      }
    }

    return false;
  }
}

