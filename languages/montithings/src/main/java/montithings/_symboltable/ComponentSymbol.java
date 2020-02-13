// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTAutomatonBehavior;
import montiarc._ast.ASTElement;
import montiarc._symboltable.PortSymbol;
import montithings._ast.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Component
 *
 * @author (last commit) kirchhof
 */
public class ComponentSymbol extends montiarc._symboltable.ComponentSymbol {
  public ComponentSymbol(String name) {
    super(name);
  }

  public ComponentSymbol(String name, SymbolKind kind) {
    super(name, kind);
  }

  /* ============================================================ */
  /* ========================= Markings ========================= */
  /* ============================================================ */

  /**
   * Checks whether this is an interface component
   * @return true iff this component uses is marked as an interface component
   */
  public boolean isInterfaceComponent() {
    if (!(getAstNode().isPresent() || getAstNode().get() instanceof ASTComponent)) {
      return false;
    }
    ASTComponent component = (ASTComponent) getAstNode().get();
    return component.isInterface();
  }

  /**
   * Checks whether this is a time-synchronous component
   * @return true iff this component uses the stereotype "timesync"
   */
  public boolean isTimeSync() {
    return getStereotype().containsKey("timesync");
  }

  /* ============================================================ */
  /* ================= Assumptions & Guarantees ================= */
  /* ============================================================ */

  /**
   * Gets all ASTAssumptions within this component's body
   * @return unsorted list of all ASTAssumptions
   */
  public List<ASTAssumption> getAssumptions() {
    List<ASTAssumption> list =  ((ASTComponent)getAstNode().get())
        .getBody()
        .getElementList().stream()
        .filter(e -> e instanceof ASTAssumption)
        .map(e -> ((ASTAssumption) e))
        .collect(Collectors.toList());
    return list;
  }

  /**
   * Gets all ASTGuarantees within this component's body
   * @return unsorted list of all ASTGuarantees
   */
  public List<ASTGuarantee> getGuarantees() {
    List<ASTGuarantee> list =  ((ASTComponent)getAstNode().get())
        .getBody()
        .getElementList().stream()
        .filter(e -> e instanceof ASTGuarantee)
        .map(e -> ((ASTGuarantee) e))
        .collect(Collectors.toList());
    return list;
  }

  /* ============================================================ */
  /* =========================  Ports  ========================== */
  /* ============================================================ */

  /**
   * Returns all ports that appear in any batch statements
   * @return unsorted list of all ports for which a batch statement exists
   */
  public List<PortSymbol> getPortsInBatchStatement() {
    List<String> names = getAstComponent()
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(e -> e instanceof ASTBatchStatement)
        .flatMap(e -> ((ASTBatchStatement) e).getBatchPortsList().stream())
        .collect(Collectors.toList());

    List<PortSymbol> ports = new ArrayList<>();
    Scope s = getSpannedScope();
    for (String name : names) {
      Optional<PortSymbol> resolve = s.resolve(name, PortSymbol.KIND);
      resolve.ifPresent(ports::add);
    }
    return ports;
  }

  /**
   * Find all ports of a component that DON'T appear in any batch statement
   * @return unsorted list of all ports NOT in any batch statement
   */
  public List<PortSymbol> getPortsNotInBatchStatements() {
    return getAllIncomingPorts()
        .stream()
        .filter(p -> !getPortsInBatchStatement().contains(p))
        .collect(Collectors.toList());
  }

  /**
   * Returns true iff the port appears in a batch expression
   *
   * @param port the port to be checked
   * @return true iff port is in any batch statement
   */
  public Boolean isBatchPort(PortSymbol port) {
    return getPortsInBatchStatement().stream()
        .anyMatch(p -> p.equals(port));
  }

  /**
   * Returns a list of ResourcePortSymbols for resources in the component
   * @return ResourcePortSymbols in component
   */
  public List<ResourcePortSymbol> getResourcePortsInComponent() {
    return getAstComponent()
        .getBody()
        .getElementList()
        .stream()
        .filter(p -> p instanceof ASTResourceInterface)
        .flatMap(p -> ((ASTResourceInterface) p)
            .getResourcePortList()
            .stream())
        .map(e -> (ResourcePortSymbol) e.getSymbolOpt().get())
        .collect(Collectors.toList());
  }

  /* ============================================================ */
  /* ========================= Behavior ========================= */
  /* ============================================================ */

  /**
   * True iff component contains if-then-else behavior
   * @return True iff component contains if-then-else behavior
   */
  public Boolean hasExecutionStatement() {
    return getExecutionStatements().size() > 0;
  }

  /**
   * Get list of execution statements sorted by priority
   * @return list of execution statements sorted by priority
   */
  public List<ASTExecutionIfStatement> getExecutionStatements() {
    return getAstComponent()
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTExecutionBlock)
        .flatMap(e -> ((ASTExecutionBlock) e).getExecutionStatementList().stream())
        .filter(e -> e instanceof ASTExecutionIfStatement)
        .map(e -> ((ASTExecutionIfStatement) e))
        .sorted(
            Comparator.comparing(e -> e.getPriorityOpt().orElse(MontiThingsMill.intLiteralBuilder()
                .setSource("1")
                .build())
                .getValue()))
        .collect(Collectors.toList());
  }

  /**
   * Get Else Statement if one exists
   * @return the (only) else statement within this component's body, Optional.empty if none exists
   */
  public Optional<ASTExecutionElseStatement> getElseStatement() {
    return getAstComponent()
        .getBody()
        .getElementList()
        .stream()
        .filter(e -> e instanceof ASTExecutionBlock)
        .flatMap(e -> ((ASTExecutionBlock) e).getExecutionStatementList().stream())
        .filter(e -> e instanceof ASTExecutionElseStatement)
        .map(e -> (ASTExecutionElseStatement) e)
        .findFirst();
  }

  /**
   * Checks if this component uses an automaton
   * @return true iff this component's body contains an automaton
   */
  public boolean containsAutomaton() {
    for (ASTElement element : getAstComponent().getBody().getElementList()) {
      if (element instanceof ASTAutomatonBehavior) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the ASTComponent for this component
   */
  private ASTComponent getAstComponent() {
    if (!(getAstNode().isPresent() || getAstNode().get() instanceof ASTComponent)) {
      Log.error("ComponentSymbol \"" + getFullName() + "\" has no ASTComponent");
    }
    return ((ASTComponent) getAstNode().get());
  }
}
