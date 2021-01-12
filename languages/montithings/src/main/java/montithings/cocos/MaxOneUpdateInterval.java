// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import arcbasis._symboltable.ComponentTypeSymbol;
import clockcontrol._ast.ASTCalculationInterval;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montithings.util.MontiThingsError;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that the update interval is uniquely defined
 */
public class MaxOneUpdateInterval implements ArcBasisASTComponentTypeCoCo {

  @Override public void check(ASTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    final ComponentTypeSymbol component = node.getSymbol();

    Set<ASTCalculationInterval> calculationIntervals =
      component.getAstNode().getBody().getArcElementList().stream()
        .filter(ASTCalculationInterval.class::isInstance)
        .map(ASTCalculationInterval.class::cast)
        .collect(Collectors.toSet());

    if (calculationIntervals.size() > 1) {
      Log.error(String.format(MontiThingsError.ONLY_ONE_UPDATE_INTERVAL.toString(), node.getName()),
        node.get_SourcePositionStart());
    }
  }
}
