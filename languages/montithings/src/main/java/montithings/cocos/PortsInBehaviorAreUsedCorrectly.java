// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTInitBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._visitor.FindIncomingPorts;
import montithings.util.MontiThingsError;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Checks that ports are used correctly in behavior blocks, i.e. that
 * - behavior does not reference undefined port names
 * - only one behavior exists per port
 * - every incoming port is can be used by some behavior
 */
public class PortsInBehaviorAreUsedCorrectly implements MontiThingsASTMTComponentTypeCoCo {

  @Override
  public void check(ASTMTComponentType node) {
    if (node.isPresentSymbol()) {
      List<PortSymbol> incomingPorts = node.getSymbol().getAllIncomingPorts();
      List<PortSymbol> unusedIncomingPorts = node.getSymbol().getAllIncomingPorts();
      List<Set<PortSymbol>> setsOfPorts = new ArrayList<>();
      for (ASTInitBehavior initBehavior : elementsOf(node.getSymbol()).filter(
          ASTInitBehavior.class::isInstance).map(ASTInitBehavior.class::cast)
        .filter(e -> !e.isEmptyNames()).collect(Collectors.toList())) {
        Set<PortSymbol> portsInBehavior = getReferencedPorts(initBehavior.getMCJavaBlock());
        for (PortSymbol portSymbolInBehavior : portsInBehavior) {
          boolean found = false;
          for (Optional<PortSymbol> portSymbolAtBehaviorTop : initBehavior.getNamesSymbolList()) {
            if (portSymbolAtBehaviorTop.isPresent()) {
              if (portSymbolAtBehaviorTop.get().equals(portSymbolInBehavior)) {
                found = true;
              }
            }
            else {
              //shouldn't happen if symbol table is built properly
              Log.error(String.format(MontiThingsError.BEHAVIOR_REFERENCES_INVALID_PORT.toString(),
                      initBehavior.getNameList().toString(), node.getSymbol().getName(), "invalid port"));
            }
          }
          if (!found) {
            Log.error(String.format(MontiThingsError.BEHAVIOR_USES_UNDECLARED_PORT.toString(),
                    initBehavior.getNameList().toString(), node.getSymbol().getName() + "Init",
                    portSymbolInBehavior.getName()));
          }
        }
      }
      for (ASTBehavior behavior : elementsOf(node.getSymbol()).filter(ASTBehavior.class::isInstance)
        .map(ASTBehavior.class::cast).filter(e -> !e.isEmptyNames()).collect(Collectors.toList())) {
        Set<PortSymbol> portsInBehavior = getReferencedPorts(behavior.getMCJavaBlock());
        for (PortSymbol portSymbolInBehavior : portsInBehavior) {
          boolean found = false;
          for (Optional<PortSymbol> portSymbolAtBehaviorTop : behavior.getNamesSymbolList()) {
            if (portSymbolAtBehaviorTop.isPresent()) {
              if (portSymbolAtBehaviorTop.get().equals(portSymbolInBehavior)) {
                found = true;
              }
            }
            else {
              //shouldn't happen if symbol table is built properly
              Log.error(String.format(MontiThingsError.BEHAVIOR_REFERENCES_INVALID_PORT.toString(),
                behavior.getNameList().toString(), node.getSymbol().getName(), "invalid port"));
            }
          }
          if (!found) {
            Log.error(String.format(MontiThingsError.BEHAVIOR_USES_UNDECLARED_PORT.toString(),
              behavior.getNameList().toString(), node.getSymbol().getName(),
              portSymbolInBehavior.getName()));
          }
        }
        Set<PortSymbol> setOfPorts = new HashSet<>();
        for (Optional<PortSymbol> portSymbol : behavior.getNamesSymbolList()) {
          if (portSymbol.isPresent()) {
            if (!incomingPorts.contains(portSymbol.get())) {
              Log.error(String.format(MontiThingsError.BEHAVIOR_REFERENCES_INVALID_PORT.toString(),
                behavior.getNameList().toString(), node.getSymbol().getName(),
                portSymbol.get().getName()));
            }
            if (!nonPortSpecificBehaviorExists(node.getSymbol())) {
              unusedIncomingPorts.remove(portSymbol.get());
            }
            if (setOfPorts.contains(portSymbol.get())) {
              Log.error("Port " + portSymbol.get().getName()
                + " declared twice in port-specific behavior with" +
                "ports " + behavior.getNameList());
            }
            setOfPorts.add(portSymbol.get());
          }
          else {
            //shouldn't happen if symbol table is built properly
            Log.error(String.format(MontiThingsError.BEHAVIOR_REFERENCES_INVALID_PORT.toString(),
              behavior.getNameList().toString(), node.getSymbol().getName(), "invalid port"));
          }
        }
        for (Set<PortSymbol> portSymbolSet : setsOfPorts) {
          if (setOfPorts.containsAll(portSymbolSet)) {
            if (portSymbolSet.containsAll(setOfPorts)) {
              Log.error(String.format(MontiThingsError.MULTIPLE_BEHAVIORS_SAME_PORTS.toString(),
                behavior.getNameList().toString(), node.getSymbol().getName()));
            }
            else {
              Log.warn(String.format(MontiThingsError.BEHAVIOR_PORTS_USED_ALREADY.toString(),
                node.getSymbol().getName(), behavior.getNameList().toString()));
            }
          }
        }
        setsOfPorts.add(setOfPorts);
      }
      if (!nonPortSpecificBehaviorExists(node.getSymbol()) &&
        portSpecificBehaviorExists(node.getSymbol()) && !unusedIncomingPorts.isEmpty()) {
        List<String> unusedIncomingPortsNames = new ArrayList<>();
        for (PortSymbol portSymbol : unusedIncomingPorts) {
          unusedIncomingPortsNames.add(portSymbol.getName());
        }
        Log.warn(String.format(MontiThingsError.INCOMING_PORTS_NOT_USED.toString(),
          unusedIncomingPortsNames.toString(), node.getSymbol().getName()));
      }
    }
  }

  public static Stream<ASTArcElement> elementsOf(ComponentTypeSymbol comp) {
    return comp.getAstNode().getBody().getArcElementList().stream();
  }

  protected Set<PortSymbol> getReferencedPorts(ASTMCJavaBlock block) {
    FindIncomingPorts portVisitor = new FindIncomingPorts();
    block.accept(portVisitor.createTraverser());
    return portVisitor.getReferencedPorts();
  }

  protected boolean nonPortSpecificBehaviorExists(ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast)
      .anyMatch(ASTBehavior::isEmptyNames);
  }

  protected boolean portSpecificBehaviorExists(ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast)
      .anyMatch(e -> !e.isEmptyNames());
  }
}