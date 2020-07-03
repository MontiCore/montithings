// (c) https://github.com/MontiCore/monticore
package cdlangextension._cocos;

/**
 * Context Conditions for CDLangExtension language
 *
 * @author (last commit) Julian Krebber
 */
public class CDLangExtensionCoCos {
  public static CDLangExtensionCoCoChecker createChecker() {
    final CDLangExtensionCoCoChecker checker = new CDLangExtensionCoCoChecker();
    return checker
        .addCoCo((CDLangExtensionASTCDLangExtensionUnitCoCo) new ImportNameUnique())
        .addCoCo((CDLangExtensionASTDepLanguageCoCo) new ImportNameUnique())
        .addCoCo(new ImportNameExists())
        .addCoCo(new ImportNameNotEmpty());
  }
}
