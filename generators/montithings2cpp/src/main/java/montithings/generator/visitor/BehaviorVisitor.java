// (c) https://github.com/MontiCore/monticore
package montithings.generator.visitor;

import arcbasis._ast.ASTJavaPBehavior;
import arcbasis._visitor.MontiArcVisitor;

import java.util.Optional;

/**
 * Visitor for getting JavaPBehavior from AST.
 *
 * @author Jerome Pfeiffer
 * @version $Revision$, $Date$
 */
public class BehaviorVisitor implements MontiArcVisitor {

  Optional<ASTJavaPBehavior> javaPBehavior = Optional.empty();

  /**
   * @see arcbasis._visitor.MontiArcVisitor#visit(arcbasis._ast.ASTJavaPBehavior)
   */
  @Override
  public void visit(ASTJavaPBehavior node) {
    javaPBehavior = Optional.of(node);
  }

  /**
   * @return javaPBehavior
   */
  public Optional<ASTJavaPBehavior> getJavaPBehavior() {
    return this.javaPBehavior;
  }

}
