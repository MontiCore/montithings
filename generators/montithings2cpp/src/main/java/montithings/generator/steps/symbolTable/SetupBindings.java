// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.symbolTable;

import bindings.BindingsTool;
import bindings._symboltable.IBindingsGlobalScope;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

public class SetupBindings extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    BindingsTool bindingsTool = new BindingsTool();
    bindingsTool.setMtGlobalScope(state.getSymTab());
    IBindingsGlobalScope binTab = bindingsTool.initSymbolTable(state.getModelPath());

    state.setBinTab(binTab);
    state.setBindingsTool(bindingsTool);
  }

}
