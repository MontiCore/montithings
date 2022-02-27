// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.symbolTable;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

public class SetupCD4C extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    CD4CodeMill.reset();
    CD4CodeMill.init();
    CD4CodeMill.globalScope().clear();
    ICD4CodeGlobalScope cd4MTGlobalScope = CD4CodeMill.globalScope();
    cd4MTGlobalScope.setModelPath(state.getMcModelPath());
  }

}
