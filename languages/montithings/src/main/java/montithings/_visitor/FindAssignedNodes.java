// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsHandler;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsTraverser;
import de.monticore.expressions.assignmentexpressions._visitor.AssignmentExpressionsVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import montithings.MontiThingsMill;

import java.util.HashSet;
import java.util.Set;

/**
 * Find assignment expressions that assign a value to a name expression (e.g. variable or port)
 */
public class FindAssignedNodes
  implements AssignmentExpressionsVisitor2, AssignmentExpressionsHandler {

  protected AssignmentExpressionsTraverser traverser;

  protected Set<ASTNameExpression> referencedAstNodes = new HashSet<>();

  @Override public void visit(ASTAssignmentExpression node) {
    if (node.getLeft() instanceof ASTNameExpression) {
      referencedAstNodes.add((ASTNameExpression) node.getLeft());
    }
  }

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4AssignmentExpressions(this);
    traverser.setAssignmentExpressionsHandler(this);
    return traverser;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Set<ASTNameExpression> getReferencedAstNodes() {
    return referencedAstNodes;
  }

  @Override public AssignmentExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(
    AssignmentExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
