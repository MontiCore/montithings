// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._symboltable.PortSymbol;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import montithings.MontiThingsMill;
import montithings._symboltable.IMontiThingsScope;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Find all incoming ports referenced by NameExpressions
 */
public class FindIncomingPorts implements ExpressionsBasisVisitor2 {
  protected Set<PortSymbol> referencedPorts = new HashSet<>();

  protected Set<ASTNameExpression> referencedPortsAstNodes = new HashSet<>();

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4ExpressionsBasis(this);
    return traverser;
  }

  @Override public void visit(ASTNameExpression node) {
    IMontiThingsScope scope = (IMontiThingsScope) node.getEnclosingScope();
    Optional<PortSymbol> port = scope.resolvePort(node.getName());

    if (port.isPresent() && port.get().isIncoming()) {
      referencedPorts.add(port.get());
      referencedPortsAstNodes.add(node);
    }
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Set<PortSymbol> getReferencedPorts() {
    return referencedPorts;
  }

  public Set<ASTNameExpression> getReferencedPortsAstNodes() {
    return referencedPortsAstNodes;
  }
}
