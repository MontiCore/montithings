// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montithings._symboltable.IMontiThingsScope;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class FindIncomingPorts implements MontiThingsVisitor {
  protected Set<PortSymbol> referencedPorts = new HashSet<>();

  protected Set<ASTNameExpression> referencedPortsAstNodes = new HashSet<>();

  @Override public void visit(ASTNameExpression node) {
    IMontiThingsScope scope = (IMontiThingsScope) node.getEnclosingScope();
    Optional<PortSymbol> port = scope.resolvePort(node.getName());

    if (port.isPresent() && port.get().isIncoming()) {
      referencedPorts.add(port.get());
      referencedPortsAstNodes.add(node);
    }
  }

  public Set<PortSymbol> getReferencedPorts() {
    return referencedPorts;
  }

  public Set<ASTNameExpression> getReferencedPortsAstNodes() {
    return referencedPortsAstNodes;
  }
}
