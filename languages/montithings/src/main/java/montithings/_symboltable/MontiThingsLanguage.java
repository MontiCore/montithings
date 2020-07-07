// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

public class MontiThingsLanguage extends MontiThingsLanguageTOP {

  public static final String LANGUAGE_NAME = "MontiThings";

  public static final String FILE_ENDING = "mt";

  protected MontiThingsModelLoader modelLoader;

  public MontiThingsLanguage(String langName, String fileEnding) {
    super(langName, fileEnding);
    modelLoader = super.getModelLoader();
  }

  public MontiThingsLanguage() {
    super(LANGUAGE_NAME, FILE_ENDING);
    modelLoader = super.getModelLoader();
  }

  @Override
  protected MontiThingsModelLoader provideModelLoader() {
    return new MontiThingsModelLoader(this);
  }

  @Override
  public String getSymbolFileExtension() {
    return null;
  }

}
