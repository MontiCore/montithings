// (c) https://github.com/MontiCore/monticore
package cdlangextension._cocos;

import cdlangextension._ast.ASTCDEImportStatement;
import cdlangextension._ast.ASTCDLangExtensionUnit;
import cdlangextension._ast.ASTDepLanguage;
import cdlangextension.util.CDLangExtensionError;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * Checks if an import statement or language was used multiple times.
 * TODO check uniqueness in multiple files.
 */
public class ImportNameUnique
  implements CDLangExtensionASTDepLanguageCoCo, CDLangExtensionASTCDLangExtensionUnitCoCo {
  @Override
  public void check(ASTDepLanguage node) {
    Set<String> names = new HashSet<>();
    for (ASTCDEImportStatement name : node.getCDEImportStatementList()) {
      if (!names.contains(name.getName())) {
        names.add(name.getName());
      }
      else {
        Log.error(String.format(CDLangExtensionError.AMBIGUOUS_IMPORT_NAME.toString(),
          name.getName(),
          name.get_SourcePositionStart().toString(), node.getName()));
      }
    }
  }

  @Override
  public void check(ASTCDLangExtensionUnit node) {
    Set<String> names = new HashSet<>();
    for (ASTDepLanguage name : node.getDepLanguageList()) {
      if (!names.contains(name.getName())) {
        names.add(name.getName());
      }
      else {
        Log.error(String
          .format(CDLangExtensionError.AMBIGUOUS_LANGUAGE_NAME.toString(), name.getName(),
            name.get_SourcePositionStart().toString()));
      }
    }
  }
}
