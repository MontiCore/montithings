// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;

import java.util.HashSet;
import java.util.Set;

/**
 * Find assignment expressions that assign a value to a name expression (e.g. variable or port)
 */
public class FindAssignedNodes implements MontiThingsVisitor {

  protected Set<ASTNameExpression> referencedAstNodes = new HashSet<>();

  @Override public void visit(ASTAssignmentExpression node) {
    if (node.getLeft() instanceof ASTNameExpression) {
      referencedAstNodes.add((ASTNameExpression) node.getLeft());
    }
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Set<ASTNameExpression> getReferencedAstNodes() {
    return referencedAstNodes;
  }
}
