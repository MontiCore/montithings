/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTExecutionBlock;
import montithings._ast.ASTExecutionElseStatement;
import montithings._ast.ASTExecutionIfStatement;
import montithings._ast.ASTExecutionStatement;
import montithings._cocos.MontiThingsASTExecutionBlockCoCo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that Execution blocks have at least one IfStatement
 * and exactly one ElseStatement
 *
 * @author (last commit)
 */
public class ExecutionBlockWellFormed implements MontiThingsASTExecutionBlockCoCo {
  @Override
  public void check(ASTExecutionBlock node) {
    List<ASTExecutionStatement> elseStatements = node
            .getExecutionStatementList()
            .stream()
            .filter(e -> e instanceof ASTExecutionElseStatement)
            .collect(Collectors.toList());

    List<ASTExecutionStatement> ifStatements = node
            .getExecutionStatementList()
            .stream()
            .filter(e -> e instanceof ASTExecutionIfStatement)
            .collect(Collectors.toList());

    if (elseStatements.isEmpty() || ifStatements.isEmpty()){
      Log.error("0xMT104 Execution Blocks should contain at least one If Statement " +
                      "and one Else Statement.",
              node.get_SourcePositionStart());
    }

    if (elseStatements.size() > 1){
      Log.error("0xMT105 Execution Blocks should not contain more than one Else Statement.");
    }
  }
}
