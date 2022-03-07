// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.symbolTable;

import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import mtconfig.MTConfigTool;
import mtconfig._symboltable.IMTConfigGlobalScope;

public class SetupMTCFG extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    MTConfigTool mtConfigTool = new MTConfigTool();
    mtConfigTool.setMtGlobalScope(state.getSymTab());
    IMTConfigGlobalScope mtConfigGlobalScope = mtConfigTool.initSymbolTable(state.getModelPath());
    state.getConfig().setMtConfigScope(mtConfigGlobalScope);
    mtConfigTool.processFiles(state.getModels().getMTConfig());
    state.setMtConfigGlobalScope(mtConfigGlobalScope);
    state.setMtConfigTool(mtConfigTool);
  }

}
