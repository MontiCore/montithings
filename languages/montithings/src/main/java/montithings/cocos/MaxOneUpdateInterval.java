// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montithings._ast.ASTCalculationInterval;
import montithings._ast.ASTControlBlock;
import montithings._ast.ASTControlStatement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks that the update interval is uniquely defined
 *
 * @author (last commit)
 */
public class MaxOneUpdateInterval implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent node) {
    List<ASTControlStatement> collect = node.getBody().getElementList()
            .stream()
            .filter(ASTControlBlock.class::isInstance)
            .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
            .filter(ASTCalculationInterval.class::isInstance)
            .collect(Collectors.toList());

    if (collect.size() > 1){
      Log.error("0xMT132 Update intervals should only be defined once in " + node.getName(),
              collect.get(0).get_SourcePositionStart());
    }
  }
}
