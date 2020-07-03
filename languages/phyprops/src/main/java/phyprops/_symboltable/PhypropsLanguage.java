// (c) https://github.com/MontiCore/monticore
package phyprops._symboltable;

/**
 * Phyprops language that contains file_ending information and adds resolving information.
 *
 * @author (last commit) Julian Krebber
 */
public class PhypropsLanguage extends PhypropsLanguageTOP {

  public static final String FILE_ENDING = "phyProp";

  public PhypropsLanguage() {
    super("Physical Properties and Capabilities Language", FILE_ENDING);
  }

  public  PhypropsLanguage(String langName,String fileEnding)  {
    super(langName, fileEnding);
  }

  @Override
  protected PhypropsModelLoader provideModelLoader() {
    return new PhypropsModelLoader(this);
  }

  @Override
  public String getSymbolFileExtension() {
    return null;
  }
}
