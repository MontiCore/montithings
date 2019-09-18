/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;

/**
 * TODO
 *
 * @author (last commit) Joshua Fürste
 */
public class NoJavaImportStatements implements MontiArcASTComponentCoCo {

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
    if (comp.getImports().size() > 2)
    Log.error("0xMT124 There should be no Java Imports in MontiThings component",
            node.get_SourcePositionStart());
  }
}
