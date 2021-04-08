// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import montithings._ast.ASTPublishPort;
import montithings._visitor.MontiThingsVisitor;

import java.util.*;

/**
 * Finds all ports that are referenced by a "publish" statement
 */
public class FindPublishedPortsVisitor implements MontiThingsVisitor {

  protected Set<PortSymbol> publishedPorts = new HashSet<>();

  @Override public void visit(ASTPublishPort node) {
    Preconditions.checkArgument(node != null);
    List<Optional<PortSymbol>> ports = getPublishedPorts(node);
    for (Optional<PortSymbol> port : ports) {
      port.ifPresent(portSymbol -> publishedPorts.add(portSymbol));
    }
  }

  // Does more or less the same as node.getPublishedPortsSymbolList() because
  // node.getPublishedPortsSymbolList() is broken in MC6.5; it will break the
  // symbol table when being executed the second time. Can be reproduced with
  // component.getOutgoingPorts().get(0).getComponent()
  public List<Optional<PortSymbol>> getPublishedPorts(ASTPublishPort node) {
    List<Optional<PortSymbol>> result = new ArrayList<>();
    for (String port : node.getPublishedPortsList()) {
      result.add(node.getEnclosingScope().resolvePort(port));
    }
    return result;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Set<PortSymbol> getPublishedPorts() {
    return publishedPorts;
  }
}
