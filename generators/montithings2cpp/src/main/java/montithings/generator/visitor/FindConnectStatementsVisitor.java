package montithings.generator.visitor;

import behavior._ast.ASTConnectStatement;
import behavior._visitor.BehaviorHandler;
import behavior._visitor.BehaviorTraverser;
import behavior._visitor.BehaviorVisitor2;
import montithings.MontiThingsMill;
import montithings._visitor.MontiThingsTraverser;

import java.util.HashSet;
import java.util.Set;

public class FindConnectStatementsVisitor implements BehaviorVisitor2, BehaviorHandler {

  protected BehaviorTraverser traverser;

  protected Set<ASTConnectStatement> connectStatements = new HashSet<>();

  @Override public void visit(ASTConnectStatement node) {
    connectStatements.add(node);
  }

  public MontiThingsTraverser createTraverser() {
    MontiThingsTraverser traverser = MontiThingsMill.traverser();
    traverser.add4Behavior(this);
    traverser.setBehaviorHandler(this);
    return traverser;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public Set<ASTConnectStatement> getConnectStatements() {
    return connectStatements;
  }

  @Override public BehaviorTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(BehaviorTraverser traverser) {
    this.traverser = traverser;
  }
}
