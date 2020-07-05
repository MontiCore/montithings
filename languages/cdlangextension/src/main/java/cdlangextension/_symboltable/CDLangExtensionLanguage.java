// (c) https://github.com/MontiCore/monticore
package cdlangextension._symboltable;

/**
 * CDLangExtension language that contains file_ending information and adds resolving information.
 *
 * @author (last commit) Julian Krebber
 */
public class CDLangExtensionLanguage extends CDLangExtensionLanguageTOP {

  public static final String FILE_ENDING = "cde";

  public CDLangExtensionLanguage() {
    super("CD 4 Analysis Language Extension", FILE_ENDING);
  }

  public  CDLangExtensionLanguage(String langName,String fileEnding)  {
    super(langName, fileEnding);
  }

  @Override
  protected CDLangExtensionModelLoader provideModelLoader() {
    return new CDLangExtensionModelLoader(this);
  }

  @Override
  public String getSymbolFileExtension() {
    return null;
  }
}
