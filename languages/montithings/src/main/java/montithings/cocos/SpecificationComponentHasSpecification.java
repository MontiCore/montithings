/* (c) https://github.com/MontiCore/monticore */
package montithings.cocos;

import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTAssumption;
import montithings._ast.ASTComponent;
import montithings._ast.ASTGuarantee;
import montithings._cocos.MontiThingsASTComponentCoCo;

import java.util.stream.Collectors;

/**
 * TODO
 *
 * @author (last commit) Kirchhof
 * @version , 05.03.2020
 * @since
 */
public class SpecificationComponentHasSpecification implements MontiThingsASTComponentCoCo {
  @Override public void check(ASTComponent node) {
    if (node.isSpecification()) {
      int assumptionCount = node.getBody()
          .getElementList().stream()
          .filter(e -> e instanceof ASTAssumption).collect(Collectors.toList()).size();
      int guaranteeCount = node.getBody()
          .getElementList().stream()
          .filter(e -> e instanceof ASTGuarantee).collect(Collectors.toList()).size();
      if ((assumptionCount + guaranteeCount) == 0) {
        Log.error("0xMT128 Specification component " + node.getName()
            + " contains neither assumptions nor guarantees");
      }
    }
  }
}
