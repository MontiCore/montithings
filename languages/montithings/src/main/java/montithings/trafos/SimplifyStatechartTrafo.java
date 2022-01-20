// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import de.monticore.scbasis._ast.ASTSCEmptyBody;
import de.monticore.scbasis._ast.ASTSCModifier;
import de.monticore.scbasis._ast.ASTSCStatechartElement;
import de.monticore.scbasis._ast.ASTSCTransition;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTArcStatechart;
import montithings._ast.ASTMTSCState;
import montithings._ast.ASTMTSCTransition;
import montithings._visitor.MontiThingsTraverser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimplifyStatechartTrafo extends BasicTransformations implements MontiThingsTrafo, MontiThingsTraverser {

  @Override
  public void visit(ASTArcStatechart node) {
    List<ASTSCStatechartElement> toAdd = new ArrayList<>();
    List<ASTSCStatechartElement> toRemove = new ArrayList<>();
    for (ASTSCStatechartElement statechartElement : node.getSCStatechartElementList()) {
      if (statechartElement instanceof ASTMTSCState) {
        ASTSCModifier modifier = MontiThingsMill.sCModifierBuilder().setStereotypeAbsent()
            .setFinal(((ASTMTSCState) statechartElement).getSCModifier().isFinal())
            .setInitial(((ASTMTSCState) statechartElement).getSCModifier().isInitial()).build();
        for (String name : ((ASTMTSCState) statechartElement).getNameList()) {
          ASTSCEmptyBody emptyBody = MontiThingsMill.sCEmptyBodyBuilder().build();
          toAdd.add(MontiThingsMill.sCStateBuilder().setName(name).setSCModifier(modifier).setSCSBody(emptyBody).build());
        }
        toRemove.remove(statechartElement);
      }
      else if (statechartElement instanceof ASTMTSCTransition) {
        ASTMTSCTransition transition = (ASTMTSCTransition) statechartElement;
        for (String sourceName : transition.getSourceNamesList()) {
          for (String targetName : transition.getTargetNamesList()) {
            ASTSCTransition newTransition = MontiThingsMill.sCTransitionBuilder().setStereotypeAbsent().
                setSourceName(sourceName).setTargetName(targetName).setSCTBody(transition.getSCTBody()).build();
            toAdd.add(newTransition);
          }
        }
        toRemove.remove(statechartElement);
      }
    }
    node.getSCStatechartElementList().removeAll(toRemove);
    node.getSCStatechartElementList().addAll(toAdd);
  }

  @Override
  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels, Collection<ASTMACompilationUnit> addedModels, ASTMACompilationUnit targetComp) {
    targetComp.accept(this);
    return originalModels;
  }
}
