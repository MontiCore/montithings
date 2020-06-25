// (c) https://github.com/MontiCore/monticore
package portextensions.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;

/**
 * Checks that ports used in batch statements exist and are incoming
 *
 * @author (last commit) JFuerste
 */
public class PortsInBatchStatementAreIncoming implements ArcBasisASTComponentTypeCoCo {

  @Override public void check(ASTComponentType node) {
    //TODO: Write me
  }

  /*
  @Override
  public void check(ASTComponent node) {

    if (!node.getSpannedScopeOpt().isPresent()) {
      Log.error(
          String.format("0xMT020 ASTComponent node \"%s\" has no " +
                  "spanned scope. Did you forget to run the " +
                  "SymbolTableCreator before checking portextensions.cocos?",
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
   */

  /**
   * TODO modify since ASTControlBlock was removed.
   * @param node
   * @return
   */
  /*
  public List<ASTBatchStatement> getBatchPortNames(ASTComponent node) {
    return new ArrayList<ASTBatchStatement>();/*node.getBody().getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(e -> e instanceof ASTBatchStatement)
        .map(e -> (ASTBatchStatement) e)
        .collect(Collectors.toList());
  }
  */
}
