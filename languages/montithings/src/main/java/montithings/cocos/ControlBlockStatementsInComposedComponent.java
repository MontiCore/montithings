/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.ComponentSymbol;
import montithings._ast.ASTCalculationInterval;
import montithings._ast.ASTControlBlock;

/**
 * TODO
 *
 * @author (last commit)
 */
public class ControlBlockStatementsInComposedComponent implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent astComponent) {
    if (!astComponent.getSubComponents().isEmpty())
    astComponent
            .getBody()
            .getElementList()
            .stream()
            .filter(e -> e instanceof ASTControlBlock)
            .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
            .filter(e -> !(e instanceof ASTCalculationInterval))
            .forEach( e-> Log.error("0xMT103 Control Blocks in Composed Components should" +
                    "  only contain Calculation intervals", e.get_SourcePositionStart()));
  }
}
