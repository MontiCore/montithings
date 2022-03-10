// (c) https://github.com/MontiCore/monticore
package behavior.types.check;

import behavior._ast.ASTAgoQualification;
import behavior._ast.ASTAttributeAssignment;
import behavior._ast.ASTObjectExpression;
import behavior._visitor.BehaviorHandler;
import behavior._visitor.BehaviorTraverser;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

public class DeriveSymTypeOfBehavior extends DeriveSymTypeOfExpression implements BehaviorHandler {

  protected BehaviorTraverser traverser;

  @Override public BehaviorTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(BehaviorTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTAgoQualification node){
    node.getExpression().accept(getTraverser());
  }

  @Override
  public void traverse(ASTObjectExpression node) {
    node.getMCObjectType().accept(getTraverser());
  }
}
