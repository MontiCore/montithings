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
    boolean emptyString = node.isPresentString()
      && node.getString().isEmpty();

    boolean emptyAngledImport = node.isPresentAngledString()
      && node.getAngledString().isEmpty();

    boolean emptyQualifiedName = node.isPresentMCQualifiedName()
      && node.getMCQualifiedName().isEmptyParts();

    boolean emptyColonName = node.isPresentCDEQualifiedColonName()
      && node.getCDEQualifiedColonName().isEmptyParts();

    boolean emptyDoubleColon = node.isPresentCDEQualifiedDoubleColonName()
      && node.getCDEQualifiedDoubleColonName().isEmptyParts();

    if (emptyString
      || emptyAngledImport
      || emptyQualifiedName
      || emptyColonName
      || emptyDoubleColon) {
      Log.error(String.format(CDLangExtensionError.EMPTY_IMPORT_FIELD.toString(),
        node.get_SourcePositionStart().toString()));
    }
  }
}
