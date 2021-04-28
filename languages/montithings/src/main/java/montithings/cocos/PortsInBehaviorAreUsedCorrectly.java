// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.collect.FluentIterable;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._visitor.FindIncomingPorts;

import java.util.*;

public class PortsInBehaviorAreUsedCorrectly implements MontiThingsASTMTComponentTypeCoCo {


  @Override
  public void check(ASTMTComponentType node) {
    if (node.isPresentSymbol()) {
      List<PortSymbol> incomingPorts = node.getSymbol().getAllIncomingPorts();
      List<PortSymbol> unusedIncomingPorts = node.getSymbol().getAllIncomingPorts();
      List<Set<PortSymbol>> setsOfPorts = new ArrayList<>();
      for (ASTBehavior behavior : elementsOf(node.getSymbol()).filter(ASTBehavior.class)
              .filter(e -> !e.isEmptyNames()).toList()) {
        Set<PortSymbol> portsInBehavior = getReferencedPorts(behavior.getMCJavaBlock());
        for (PortSymbol portSymbolInBehavior : portsInBehavior) {
          boolean found = false;
          for (Optional<PortSymbol> portSymbolAtBehaviorTop : behavior.getNamesSymbolList()) {
            if (portSymbolAtBehaviorTop.isPresent()) {
              if (portSymbolAtBehaviorTop.get().equals(portSymbolInBehavior)) {
                found = true;
              }
            } else {
              //shouldn't happen if symbol table is built properly
              Log.error("Coco2");
            }
          }
          if (!found) {
            Log.error("Coco1");
          }
        }
        Set<PortSymbol> setOfPorts = new HashSet<>();
        for (Optional<PortSymbol> portSymbol : behavior.getNamesSymbolList()) {
          if (portSymbol.isPresent()) {
            if (!incomingPorts.contains(portSymbol.get())) {
              Log.error("Coco2");
            }
            if (!nonPortSpecificBehaviorExists(node.getSymbol())) {
              unusedIncomingPorts.remove(portSymbol.get());
            }
            if (setOfPorts.contains(portSymbol.get())) {
              Log.error("Port used twice in port specific behavior");
            }
            setOfPorts.add(portSymbol.get());
          } else {
            //shouldn't happen if symbol table is built properly
            Log.error("Coco2");
          }
        }
        for (Set<PortSymbol> portSymbolSet : setsOfPorts) {
          if (setOfPorts.containsAll(portSymbolSet)) {
            if (portSymbolSet.containsAll(setOfPorts)) {
              Log.error("Coco4");
            } else {
              //TODO: change to warning
              Log.error("Coco5");
            }
          }
        }
        setsOfPorts.add(setOfPorts);
      }
      if (!nonPortSpecificBehaviorExists(node.getSymbol()) &&
              portSpecificBehaviorExists(node.getSymbol()) && !unusedIncomingPorts.isEmpty()) {
        //TODO: change to warning
        Log.error("Coco3");
      }
    }
  }

  public static FluentIterable<ASTArcElement> elementsOf(ComponentTypeSymbol comp) {
    return FluentIterable.from(comp.getAstNode().getBody().getArcElementList());
  }

  protected Set<PortSymbol> getReferencedPorts(ASTMCJavaBlock block) {
    FindIncomingPorts portVisitor = new FindIncomingPorts();
    block.accept(portVisitor);
    return portVisitor.getReferencedPorts();
  }

  protected boolean nonPortSpecificBehaviorExists(ComponentTypeSymbol comp) {
    return !(elementsOf(comp).filter(ASTBehavior.class)
            .filter(e -> e.isEmptyNames()).toList().isEmpty());
  }

  protected boolean portSpecificBehaviorExists(ComponentTypeSymbol comp) {
    return !(elementsOf(comp).filter(ASTBehavior.class)
            .filter(e -> !e.isEmptyNames()).toList().isEmpty());
  }
}