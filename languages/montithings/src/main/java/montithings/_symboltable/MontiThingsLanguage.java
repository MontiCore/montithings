/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package montithings._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import montiarc._symboltable.MontiArcLanguage;

/**
 * TODO
 *
 * @author (last commit) Joshua Fürste
 */
public class MontiThingsLanguage extends MontiThingsLanguageTOP{

  public static final String FILE_ENDING = "mt";

  public MontiThingsLanguage() {
    super("MontiThings Language", FILE_ENDING);
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new MontiThingsModelLoader(this);
  }

  @Override
  protected void initResolvingFilters() {
    super.initResolvingFilters();
    MontiArcLanguage montiarc = new MontiArcLanguage();
    addResolvingFilters(montiarc.getResolvingFilters());
  }

}
