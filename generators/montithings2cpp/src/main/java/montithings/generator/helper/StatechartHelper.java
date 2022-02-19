// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionBody;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatechartHelper {
  public static ASTArcStatechart getStatechart(ComponentTypeSymbol comp) {
    return ComponentHelper.elementsOf(comp).filter(ASTArcStatechart.class::isInstance)
      .map(ASTArcStatechart.class::cast).findFirst().get();
  }

  public static ASTSCState getInitialState(ComponentTypeSymbol comp) {
    Optional<ASTSCState> initialState = getStatechart(comp).streamInitialStates().findFirst();
    Preconditions.checkArgument(initialState.isPresent());
    return initialState.get();
  }

  public static List<ASTSCState> getStates(ComponentTypeSymbol comp) {
    return getStatechart(comp).streamStates().collect(Collectors.toList());
  }

  public static List<ASTSCTransition> getTransitions(ComponentTypeSymbol comp) {
    return getStatechart(comp).streamTransitions().collect(Collectors.toList());
  }

  public static boolean hasGuard(ASTSCTransition transition) {
    return ((ASTTransitionBody)transition.getSCTBody()).isPresentPre();
  }

  public static ASTExpression getGuard(ASTSCTransition transition) {
    return ((ASTTransitionBody)transition.getSCTBody()).getPre();
  }

  public static List<PortSymbol> getPortsInGuard(ASTSCTransition transition) {
    return ComponentHelper.getPortsInGuardExpression(getGuard(transition));
  }

  public static boolean hasAction(ASTSCTransition transition) {
    return ((ASTTransitionBody)transition.getSCTBody()).isPresentTransitionAction();
  }

  public static ASTMCBlockStatement getAction(ASTSCTransition transition) {
    return ((ASTTransitionBody)transition.getSCTBody()).getTransitionAction().getMCBlockStatement();
  }
}
