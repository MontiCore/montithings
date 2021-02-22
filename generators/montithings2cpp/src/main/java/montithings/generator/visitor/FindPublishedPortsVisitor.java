// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._symboltable.PortSymbol;
import com.google.common.base.Preconditions;
import montithings._ast.ASTPublishPort;
import montithings._visitor.MontiThingsVisitor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Finds all ports that are referenced by a "publish" statement
 */
public class FindPublishedPortsVisitor implements MontiThingsVisitor {

  protected Set<PortSymbol> publishedPorts = new HashSet<>();

  @Override public void visit(ASTPublishPort node) {
    Preconditions.checkArgument(node != null);
    List<Optional<PortSymbol>> ports = node.getPublishedPortsSymbolList();
    for (Optional<PortSymbol> port : ports) {
      port.ifPresent(portSymbol -> publishedPorts.add(portSymbol));
    }
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Set<PortSymbol> getPublishedPorts() {
    return publishedPorts;
  }
}
