// (c) https://github.com/MontiCore/monticore
package cdlangextension._cocos;

/**
 * Context Conditions for CDLangExtension language
 */
public class CDLangExtensionCoCos {
  public static CDLangExtensionCoCoChecker createChecker() {
    final CDLangExtensionCoCoChecker checker = new CDLangExtensionCoCoChecker();
    checker.addCoCo((CDLangExtensionASTCDLangExtensionUnitCoCo) new ImportNameUnique());
    checker.addCoCo((CDLangExtensionASTDepLanguageCoCo) new ImportNameUnique());
    checker.addCoCo(new ImportNameExists());
    checker.addCoCo(new ImportNameNotEmpty());
    return checker;
  }
}
