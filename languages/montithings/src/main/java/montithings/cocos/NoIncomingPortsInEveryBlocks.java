// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import arcbasis._symboltable.PortSymbol;
import behavior._ast.ASTEveryBlock;
import behavior._ast.ASTLogStatement;
import behavior._cocos.BehaviorASTEveryBlockCoCo;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings._visitor.MontiThingsVisitor;
import montithings.util.MontiThingsError;

import java.util.List;
import java.util.Optional;

public class NoIncomingPortsInEveryBlocks implements BehaviorASTEveryBlockCoCo {
  @Override public void check(ASTEveryBlock node) {
    node.getMCJavaBlock().accept(new NoIncomingPortsInEveryBlocksVisitor());
    node.getMCJavaBlock().accept(new NoIncomingPortsInEveryBlocksLogsVisitor());
  }

  protected class NoIncomingPortsInEveryBlocksVisitor implements MontiThingsVisitor {
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
  }

  protected class NoIncomingPortsInEveryBlocksLogsVisitor implements MontiThingsVisitor {
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
  }
}
