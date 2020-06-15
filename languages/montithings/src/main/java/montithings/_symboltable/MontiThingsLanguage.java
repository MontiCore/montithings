// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

public class MontiThingsLanguage extends MontiThingsLanguageTOP {

  protected MontiThingsModelLoader modelLoader = new MontiThingsModelLoader(this);

  public MontiThingsLanguage(String langName, String fileEnding) {
    super(langName, fileEnding);
  }

  public MontiThingsLanguage() {
    super("MontiThings", ".mc4");
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
