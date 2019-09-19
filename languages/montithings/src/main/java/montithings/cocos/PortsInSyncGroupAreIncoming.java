/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings.cocos;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTComponent;
import montiarc._cocos.MontiArcASTComponentCoCo;
import montiarc._symboltable.PortSymbol;
import montithings._ast.ASTControlBlock;
import montithings._ast.ASTSyncStatement;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that ports in sync statements exist and are incoming
 *
 * @author (last commit) JFuerste
 */
public class PortsInSyncGroupAreIncoming implements MontiArcASTComponentCoCo {
  @Override
  public void check(ASTComponent node) {

    if (!node.getSpannedScopeOpt().isPresent()){
      Log.error(
              String.format("0xMT020 ASTComponent node \"%s\" has no " +
                              "spanned scope. Did you forget to run the " +
                              "SymbolTableCreator before checking cocos?",
                      node.getName()));
      return;
    }

    Scope s = node.getSpannedScopeOpt().get();
    for (ASTSyncStatement syncGroup : getSyncGroups(node)) {
      for (String portName : syncGroup.getSyncedPortList()) {
        Optional<Symbol> port = s.resolve(portName, PortSymbol.KIND);
        if (!port.isPresent()){
          Log.error("0xMT113 The port " + portName + " in the sync group does not exist.",
                  syncGroup.get_SourcePositionStart());
          continue;
        }
        if (!((PortSymbol)port.get()).isIncoming()){
          Log.error("0xMT114 The port " + portName + " in the sync group is not incoming.",
                  syncGroup.get_SourcePositionStart());
        }
      }

    }

  }

  public List<ASTSyncStatement> getSyncGroups(ASTComponent node){
    return node.getBody().getElementList()
            .stream()
            .filter(e -> e instanceof ASTControlBlock)
            .flatMap(e -> ((ASTControlBlock) e).getControlStatementList().stream())
            .filter(ASTSyncStatement.class::isInstance)
            .map(ASTSyncStatement.class::cast)
            .collect(Collectors.toList());
  }
}
