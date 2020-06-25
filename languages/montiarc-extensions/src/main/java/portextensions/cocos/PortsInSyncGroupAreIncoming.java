// (c) https://github.com/MontiCore/monticore
package portextensions.cocos;

import arcbasis._ast.ASTComponentType;
import arcbasis._cocos.ArcBasisASTComponentTypeCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that ports in sync statements exist and are incoming
 */
public class PortsInSyncGroupAreIncoming implements ArcBasisASTComponentTypeCoCo {


  @Override public void check(ASTComponentType node) {
    //TODO: Write me
  }

  /*
  @Override
  public void check(ASTComponentType node) {

    if (!node.getSpannedScopeOpt().isPresent()) {
      Log.error(
          String.format("0xMT020 ASTComponent node \"%s\" has no " +
                  "spanned scope. Did you forget to run the " +
                  "SymbolTableCreator before checking portextensions.cocos?",
              node.getName()));
      return;
    }

    Scope s = node.getSpannedScopeOpt().get();
    for (ASTSyncStatement syncGroup : getSyncGroups(node)) {
      for (String portName : syncGroup.getSyncedPortList()) {
        Optional<Symbol> port = s.resolve(portName, PortSymbol.KIND);
        if (!port.isPresent()) {
          Log.error("0xMT113 The port " + portName + " in the sync group does not exist.",
              syncGroup.get_SourcePositionStart());
          continue;
        }
        if (!((PortSymbol) port.get()).isIncoming()) {
          Log.error("0xMT114 The port " + portName + " in the sync group is not incoming.",
              syncGroup.get_SourcePositionStart());
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
  public List<ASTSyncStatement> getSyncGroups(ASTComponent node) {
    return new ArrayList<>();/*node.getBody().getElementList()
        .stream()
        .filter(e -> e instanceof ASTControlBlock)
        .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
        .filter(ASTSyncStatement.class::isInstance)
        .map(ASTSyncStatement.class::cast)
        .collect(Collectors.toList());
  }*/
}
