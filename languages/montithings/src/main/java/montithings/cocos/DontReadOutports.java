// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTEveryBlock;
import montithings._cocos.MontiThingsASTBehaviorCoCo;
import montithings._cocos.MontiThingsASTMTEveryBlockCoCo;
import montithings._visitor.FindAssignedNodes;
import montithings._visitor.FindOutgoingPorts;
import montithings.util.MontiThingsError;

import java.util.Set;

/**
 * Prevents reading an outgoing port
 */
public class DontReadOutports implements MontiThingsASTBehaviorCoCo,
  MontiThingsASTMTEveryBlockCoCo {

  @Override public void check(ASTBehavior node) {
    checkBlock(node.getMCJavaBlock());
  }

  @Override public void check(ASTMTEveryBlock node) {
    checkBlock(node.getEveryBlock().getMCJavaBlock());
  }

  protected void checkBlock(ASTMCJavaBlock block) {
    Set<ASTNameExpression> allowedUsage = getAssignedNodes(block);
    for (ASTNameExpression node : getReferencedPorts(block)) {
      if (!allowedUsage.contains(node)) {
        Log.error(String.format(MontiThingsError.OUTPORT_WRITE_ONLY.toString(), node.getName()),
          node.get_SourcePositionStart());
      }
    }
  }

  protected Set<ASTNameExpression> getAssignedNodes(ASTMCJavaBlock block) {
    FindAssignedNodes assignmentVisitor = new FindAssignedNodes();
    block.accept(assignmentVisitor);
    return assignmentVisitor.getReferencedAstNodes();
  }

  protected Set<ASTNameExpression> getReferencedPorts(ASTMCJavaBlock block) {
    FindOutgoingPorts portVisitor = new FindOutgoingPorts();
    block.accept(portVisitor);
    return portVisitor.getReferencedPortsAstNodes();
  }
}
