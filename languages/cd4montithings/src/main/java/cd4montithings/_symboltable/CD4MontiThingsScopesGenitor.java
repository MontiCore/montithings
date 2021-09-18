package cd4montithings._symboltable;

import cd4montithings.CD4MontiThingsMill;
import de.se_rwth.commons.logging.Log;

public class CD4MontiThingsScopesGenitor extends CD4MontiThingsScopesGenitorTOP {
  @Override
  public void visit (cd4montithings._ast.ASTCDPort node) {
    CDPortSymbol symbol = CD4MontiThingsMill.cDPortSymbolBuilder().setName(node.getName()).build();
    if (node.isPresentType()) {
      symbol.setType(node.getType());
    }
    symbol.setIsReadOnly(false);
    if (getCurrentScope().isPresent()) {
      getCurrentScope().get().add(symbol);
    } else {
      Log.warn("0xA5021x66806 Symbol cannot be added to current scope, since no scope exists.");
    }
    // symbol -> ast
    symbol.setAstNode(node);

    // ast -> symbol
    node.setSymbol(symbol);
    node.setEnclosingScope(symbol.getEnclosingScope());

    initCDPortHP1(node.getSymbol());

  }
}
