// (c) https://github.com/MontiCore/monticore
package montithings.cocos;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.PortSymbol;
import montithings._ast.ASTBatchStatement;
import montithings._ast.ASTControlBlock;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that ports used in batch statements exist and are incoming
 *
 * @author (last commit) JFuerste
 */
public class PortsInBatchStatementAreIncoming implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent node) {

    if (!node.getSpannedScopeOpt().isPresent()) {
      Log.error(
          String.format("0xMT020 ASTComponent node \"%s\" has no " +
                  "spanned scope. Did you forget to run the " +
                  "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }

    Scope s = node.getSpannedScopeOpt().get();
    for (ASTBatchStatement batchStatement : getBatchPortNames(node)) {
      for (String portName : batchStatement.getBatchPortsList()) {
        Optional<Symbol> port = s.resolve(portName, PortSymbol.KIND);
        if (!port.isPresent()) {
          Log.error("0xMT111 The port " + portName + " does not exist in the batch statement.",
              batchStatement.get_SourcePositionStart());
          continue;
        }
        if (!((PortSymbol) port.get()).isIncoming()) {
          Log.error("0xMT112 The port " + portName + " in the batch statement is not incoming.",
              batchStatement.get_SourcePositionStart());
        }
      }
    }

  }

  public List<ASTBatchStatement> getBatchPortNames(ASTComponent node) {
    return node.getBody().getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(e -> e instanceof ASTBatchStatement)
        .map(e -> (ASTBatchStatement) e)
        .collect(Collectors.toList());
  }
}
