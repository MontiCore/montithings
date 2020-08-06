// (c) https://github.com/MontiCore/monticore
package bindings._symboltable;

/**
 * Provides basic Binding Language information
 * e.g. the standard file ending.
 *
 * @author (last commit) Julian Krebber
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
