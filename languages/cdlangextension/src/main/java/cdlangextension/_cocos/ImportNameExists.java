// (c) https://github.com/MontiCore/monticore
package cdlangextension._cocos;

import cdlangextension._ast.ASTCDEImportStatement;
import cdlangextension.util.CDLangExtensionError;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks if import names refer to CDTypeSymbols.
 *
 * @author Julian Krebber
 */
public class ImportNameExists implements CDLangExtensionASTCDEImportStatementCoCo {
  @Override
  public void check(ASTCDEImportStatement node) {
    if(!node.isPresentNameDefinition() && !node.isPresentNameSymbol()){
      Log.error(
          String.format(CDLangExtensionError.MISSING_IMPORT_NAME.toString(),
              node.getName(),node.get_SourcePositionEnd().getLine(),node.get_SourcePositionEnd().getColumn()-node.getName().length()-1));
    }
  }
}
