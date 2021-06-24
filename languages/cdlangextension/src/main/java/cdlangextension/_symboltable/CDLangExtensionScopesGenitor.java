// (c) https://github.com/MontiCore/monticore
package cdlangextension._symboltable;

import cdlangextension._ast.ASTCDEImportStatement;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

import java.util.Optional;


public class CDLangExtensionScopesGenitor extends CDLangExtensionScopesGenitorTOP {

  @Override public void visit(ASTCDEImportStatement node) {
    super.visit(node);
    Optional<TypeSymbol> symbol = node.getEnclosingScope().resolveType(node.getCdType().getQName());
    symbol.ifPresent(node::setTypeSymbol);
  }
}
