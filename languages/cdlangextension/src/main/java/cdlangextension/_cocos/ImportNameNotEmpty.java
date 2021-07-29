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
    boolean empty = false;
    if (node.isPresentString() && node.getString().equals("")) {
      empty = true;
    }
    else if (node.isPresentAngledString() && node.getAngledString().equals("")) {
      empty = true;
    }
    else if (node.isPresentMCQualifiedName() && node.getMCQualifiedName().equals("")) {
      empty = true;
    }
    else if (node.isPresentCDEQualifiedColonName() && node.getCDEQualifiedColonName().equals("")) {
      empty = true;
    }
    else if (node.isPresentCDEQualifiedDoubleColonName() && node.getCDEQualifiedDoubleColonName().equals("")) {
      empty = true;
    }
    if (empty) {
      Log.error(String.format(CDLangExtensionError.EMPTY_IMPORT_FIELD.toString(),
        node.get_SourcePositionStart().toString()));
    }
  }
}
