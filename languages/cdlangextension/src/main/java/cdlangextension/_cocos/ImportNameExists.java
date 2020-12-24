// (c) https://github.com/MontiCore/monticore
package cdlangextension._cocos;

import cdlangextension._ast.ASTCDEImportStatement;
import cdlangextension.util.CDLangExtensionError;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks if import names refer to CDTypeSymbols.
 *
 * @author Julian Krebber
 */
public class ImportNameExists implements CDLangExtensionASTCDEImportStatementCoCo {

  public void check(ASTCDEImportStatement node) {
    Optional<CDTypeSymbol> symbol = node.getEnclosingScope().resolveCDType(node.getCdType().getQName());
    if (!symbol.isPresent()) {
      Log.error(
        String.format(CDLangExtensionError.MISSING_IMPORT_NAME.toString(),
          node.getName(),node.get_SourcePositionEnd().getLine(),
          node.get_SourcePositionEnd().getColumn()-node.getName().length()-1));
    }
  }
}
