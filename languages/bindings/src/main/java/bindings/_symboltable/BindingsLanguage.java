// (c) https://github.com/MontiCore/monticore
package bindings._symboltable;

/**
 * TODO
 *
 * @author (last commit) Daniel von Mirbach
 */
public class BindingsLanguage extends BindingsLanguageTOP {

  public static final String FILE_ENDING = "mtb";

  public BindingsLanguage() {
    super("Bindings Language", FILE_ENDING);
  }

  public  BindingsLanguage(String langName,String fileEnding)  {
    super(langName, fileEnding);
  }

  @Override
  protected BindingsModelLoader provideModelLoader() {
    return new BindingsModelLoader(this);
  }

  @Override
  public String getSymbolFileExtension() {
    return null;
  }

}
