// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import arcbasis._ast.ASTPort;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._visitor.ArcBasisVisitor2;
import com.google.common.base.Preconditions;
import montithings.MontiThingsMill;

import java.util.HashSet;
import java.util.Set;

public class FindPortNamesVisitor implements ArcBasisVisitor2 {

  protected Set<String> ingoingPorts = new HashSet<>();

  protected Set<String> outgoingPorts = new HashSet<>();

  @Override
  public void visit(ASTPortDeclaration node) {
    Preconditions.checkArgument(node != null);

    for (ASTPort astPort : node.getPortList()) {
      if (node.isIncoming()) {
        ingoingPorts.add(astPort.getName());//qName);
      }
      else {
        outgoingPorts.add(astPort.getName());//qName);
      }
    }

  }

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4ArcBasis(this);
    return traverser;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Set<String> getIngoingPorts() {
    return ingoingPorts;
  }

  public Set<String> getOutgoingPorts() {
    return outgoingPorts;
  }
}
