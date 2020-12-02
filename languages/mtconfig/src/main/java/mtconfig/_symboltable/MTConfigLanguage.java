// (c) https://github.com/MontiCore/monticore
package mtconfig._symboltable;

/**
 * MTConfig language that contains file_ending information and adds resolving information.
 *
 * @author (last commit) Julian Krebber
 */
public class MTConfigLanguage extends MTConfigLanguageTOP {

  public static final String FILE_ENDING = "mtcfg";

  public MTConfigLanguage() {
    super("Physical Properties and Capabilities Language", FILE_ENDING);
  }

  public  MTConfigLanguage(String langName,String fileEnding)  {
    super(langName, fileEnding);
  }

  @Override
  protected MTConfigModelLoader provideModelLoader() {
    return new MTConfigModelLoader(this);
  }

  @Override
  public String getSymbolFileExtension() {
    return null;
  }
}
