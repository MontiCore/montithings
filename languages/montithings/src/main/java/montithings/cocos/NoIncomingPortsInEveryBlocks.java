// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.PortSymbol;
import behavior._ast.ASTEveryBlock;
import behavior._ast.ASTLogStatement;
import behavior._cocos.BehaviorASTEveryBlockCoCo;
import behavior._visitor.BehaviorVisitor2;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.se_rwth.commons.logging.Log;
import montithings.MontiThingsMill;
import montithings._symboltable.IMontiThingsScope;
import montithings._visitor.MontiThingsTraverser;
import montithings.util.MontiThingsError;

import java.util.List;
import java.util.Optional;

/**
 * Checks that every blocks do not reference incoming ports.
 * Every blocks are triggered by time, not by incoming messages.
 * Hence, it's forbidden to reference incoming ports.
 */
public class NoIncomingPortsInEveryBlocks implements BehaviorASTEveryBlockCoCo {
  @Override public void check(ASTEveryBlock node) {
    node.getMCJavaBlock().accept(new NoIncomingPortsInEveryBlocksVisitor().createTraverser());
    node.getMCJavaBlock().accept(new NoIncomingPortsInEveryBlocksLogsVisitor().createTraverser());
  }

  protected static class NoIncomingPortsInEveryBlocksVisitor implements ExpressionsBasisVisitor2 {
    @Override public void visit(ASTNameExpression node) {
      String referencedName = node.getName();
      Optional<PortSymbol> referencedPort =
        ((IMontiThingsScope) node.getEnclosingScope()).resolvePort(referencedName);

      if (referencedPort.isPresent() && referencedPort.get().isIncoming()) {
        Log.error(String.format(MontiThingsError.NO_INCOMING_PORTS_IN_EVERY_BLOCK.toString(),
          referencedName,
          referencedPort.get().getComponent().get().getFullName()));
      }
    }

    public MontiThingsTraverser createTraverser() {
      MontiThingsTraverser traverser = MontiThingsMill.traverser();
      traverser.add4ExpressionsBasis(this);
      return traverser;
    }
  }

  protected static class NoIncomingPortsInEveryBlocksLogsVisitor implements BehaviorVisitor2 {
    @Override public void visit(ASTLogStatement node) {
      List<String> referencedNames = node.getReferencedVariables();
      for (String currentName : referencedNames) {
        Optional<PortSymbol> referencedPort =
          ((IMontiThingsScope) node.getEnclosingScope()).resolvePort(currentName);

        if (referencedPort.isPresent() && referencedPort.get().isIncoming()) {
          Log.error(String.format(MontiThingsError.NO_INCOMING_PORTS_IN_EVERY_BLOCK_LOG.toString(),
            currentName,
            referencedPort.get().getComponent().get().getFullName()));
        }
      }
    }

    public MontiThingsTraverser createTraverser() {
      MontiThingsTraverser traverser = MontiThingsMill.traverser();
      traverser.add4Behavior(this);
      return traverser;
    }
  }
}
