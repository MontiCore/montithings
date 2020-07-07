// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

public class MontiThingsLanguage extends MontiThingsLanguageTOP {

  public static final String LANGUAGE_NAME = "MontiThings";

  public static final String FILE_ENDING = "mt";

  protected MontiThingsModelLoader modelLoader = new MontiThingsModelLoader(this);

  public MontiThingsLanguage(String langName, String fileEnding) {
    super(langName, fileEnding);
  }

  public MontiThingsLanguage() {
    super(LANGUAGE_NAME, FILE_ENDING);
  }

  @Override
  protected MontiThingsModelLoader provideModelLoader() {
    return this.modelLoader;
  }

  @Override
  public String getSymbolFileExtension() {
    return null;
  }

}
