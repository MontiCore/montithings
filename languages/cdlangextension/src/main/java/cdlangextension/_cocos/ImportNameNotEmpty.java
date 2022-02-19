// (c) https://github.com/MontiCore/monticore
package cdlangextension._cocos;

import cdlangextension._ast.ASTCDEImportName;
import cdlangextension.util.CDLangExtensionError;
import de.se_rwth.commons.logging.Log;

/**
 * Checks if import name were left empty.
 */
public class ImportNameNotEmpty implements CDLangExtensionASTCDEImportNameCoCo {
  @Override
  public void check(ASTCDEImportName node) {
    if (node.isPresentString() && node.getString().equals("")
      || node.isPresentAngledString() && node.getAngledString().equals("")
      || node.isPresentMCQualifiedName() && node.getMCQualifiedName().equals("")
      || node.isPresentCDEQualifiedColonName() && node.getCDEQualifiedColonName().equals("")
      || node.isPresentCDEQualifiedDoubleColonName() && node.getCDEQualifiedDoubleColonName()
      .equals("")) {
      Log.error(String.format(CDLangExtensionError.EMPTY_IMPORT_FIELD.toString(),
        node.get_SourcePositionStart().toString()));
    }
  }
}
