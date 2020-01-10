/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package bindings._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import montiarc._symboltable.MontiArcLanguage;

/**
 * TODO
 *
 * @author (last commit) Daniel von Mirbach
 */
public abstract class BindingsLanguage extends BindingsLanguageTOP{

  public static final String FILE_ENDING = "mtb";

  public BindingsLanguage() {
    super("Bindings Language", FILE_ENDING);
  }

  @Override
  protected ModelingLanguageModelLoader<? extends ASTNode> provideModelLoader() {
    return new BindingsModelLoader(this);
  }

  @Override
  protected void initResolvingFilters() {
    super.initResolvingFilters();
    MontiArcLanguage montiarc = new MontiArcLanguage();
    addResolvingFilters(montiarc.getResolvingFilters());
  }

}
