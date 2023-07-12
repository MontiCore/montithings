// (c) https://github.com/MontiCore/monticore
package montithings.util;

import arcbasis._ast.ASTArcElement;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import componenttest._ast.ASTTestBlock;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.symboltable.IScopeSpanningSymbol;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTInitBehavior;
import montithings._ast.ASTMTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._visitor.FindIncomingPorts;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PortUtil {

  public static Stream<ASTArcElement> elementsOf(ComponentTypeSymbol comp) {
    return comp.getAstNode().getBody().getArcElementList().stream();
  }

  public static List<Set<String>> getSetsPortsDeclaredByBehaviors(ASTMTComponentType node) {
    List<Set<String>> portNameSets = new ArrayList<>();
    getAstBehaviors(node).forEach(b -> portNameSets.add(new HashSet<>(b.getNameList())));
    return portNameSets;
  }

  public static List<PortSymbol> getPresentPortsOfBehavior(ASTMTBehavior behavior) {
    return behavior.getNamesSymbolList().stream()
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toList());
  }

  public static List<ASTInitBehavior> getPortSpecificInitBehaviors(ASTMTComponentType node) {
    return elementsOf(node.getSymbol())
      .filter(ASTInitBehavior.class::isInstance)
      .map(ASTInitBehavior.class::cast)
      .filter(e -> !e.isEmptyNames())
      .collect(Collectors.toList());
  }

  public static List<ASTBehavior> getAstBehaviors(ASTMTComponentType node) {
    return elementsOf(node.getSymbol())
      .filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast)
      .filter(e -> !e.isEmptyNames())
      .collect(Collectors.toList());
  }

  public static Set<PortSymbol> getReferencedPorts(ASTMCJavaBlock block) {
    FindIncomingPorts portVisitor = new FindIncomingPorts();
    block.accept(portVisitor.createTraverser());
    return portVisitor.getReferencedPorts();
  }

  public static boolean nonPortSpecificBehaviorExists(ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast)
      .anyMatch(ASTBehavior::isEmptyNames);
  }

  public static boolean portSpecificBehaviorExists(ComponentTypeSymbol comp) {
    return elementsOf(comp)
      .filter(ASTBehavior.class::isInstance)
      .map(ASTBehavior.class::cast)
      .anyMatch(e -> !e.isEmptyNames());
  }

  public static PortSymbol findPortSymbolOfTestBlock(ASTTestBlock node) {
    IScopeSpanningSymbol spanningSymbol = node.getEnclosingScope().getSpanningSymbol();
    if (spanningSymbol instanceof ComponentTypeSymbol) {
      List<PortSymbol> portSymbols = ((ComponentTypeSymbol) spanningSymbol).getAllIncomingPorts().stream()
        .filter(p -> p.getName().equals(node.getPortName())).collect(Collectors.toList());
      return portSymbols.size() == 1 ? portSymbols.get(0) : null;
    }
    return null;
  }
}
