// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;

/**
 * TODO move from control block to component, since interval was moved.
 * Checks that the update interval is uniquely defined
 */
public class MaxOneUpdateInterval implements ArcBasisASTComponentTypeCoCo {

  @Override public void check(ASTComponentType node) {
    //TODO: Write me
  }

  // MontiThings 5 version below
  /*
  @Override
  public void check(ASTComponent node) {
    List<ASTControlStatement> collect = node.getBody().getElementList()
        .stream()
        .filter(ASTControlBlock.class::isInstance)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(ASTCalculationInterval.class::isInstance)
        .collect(Collectors.toList());

    if (collect.size() > 1) {
      Log.error("0xMT132 Update intervals should only be defined once in " + node.getName(),
          collect.get(0).get_SourcePositionStart());
    }
  }
   */
}
