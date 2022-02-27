// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.symbolTable;

import cdlangextension.CDLangExtensionTool;
import cdlangextension._symboltable.ICDLangExtensionGlobalScope;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

public class SetupCDLangExtension extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    CDLangExtensionTool cdExtensionTool = new CDLangExtensionTool();
    ICDLangExtensionGlobalScope cdLangExtensionGlobalScope =
      cdExtensionTool.initSymbolTable(state.getModelPath());

    state.getConfig().setCdLangExtensionScope(cdLangExtensionGlobalScope);
    state.setCdExtensionTool(cdExtensionTool);
  }

}
