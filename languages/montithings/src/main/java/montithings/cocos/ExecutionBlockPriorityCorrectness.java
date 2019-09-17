/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTExecutionBlock;
import montithings._ast.ASTExecutionIfStatement;
import montithings._ast.ASTExecutionStatement;
import montithings._cocos.MontiThingsASTExecutionBlockCoCo;

import java.util.HashSet;

/**
 * TODO
 *
 * @author (last commit)
 */
public class ExecutionBlockPriorityCorrectness implements MontiThingsASTExecutionBlockCoCo {
  @Override
  public void check(ASTExecutionBlock node) {
    boolean noDuplicates = node.getExecutionStatementList()
            .stream()
            .filter(e -> ((ASTExecutionIfStatement) e).getPriorityOpt().isPresent())
            .map(e -> ((ASTExecutionIfStatement) e).getPriority().getValue())
            .allMatch(new HashSet<>()::add);
    if (!noDuplicates){
      Log.error("0xMT106 Priorities in Execution Blocks should be unique.", node.get_SourcePositionStart());
    }

    if (node.getExecutionStatementList().size() > 2) {
      node.getExecutionStatementList()
              .stream()
              .filter(e -> e instanceof ASTExecutionIfStatement)
              .filter(e -> !(((ASTExecutionIfStatement) e).getPriorityOpt().isPresent()))
              .forEach(e -> Log.error("0xMT107 If there is more than one if " +
                      " statement present, priorities are required", e.get_SourcePositionStart()));

    }

  }
}
