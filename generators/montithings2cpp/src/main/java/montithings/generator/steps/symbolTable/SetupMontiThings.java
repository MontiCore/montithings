// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.symbolTable;

import com.google.common.base.Preconditions;
import montithings.MontiThingsMill;
import montithings._symboltable.IMontiThingsGlobalScope;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import org.codehaus.commons.nullanalysis.NotNull;

import static montithings.util.LibraryFunctionsUtil.addAllLibraryFunctions;

public class SetupMontiThings extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    MontiThingsMill.reset();
    MontiThingsMill.init();
    MontiThingsMill.globalScope().clear();
    IMontiThingsGlobalScope symTab = createMTGlobalScope(state);
    state.setSymTab(symTab);
  }

  protected IMontiThingsGlobalScope createMTGlobalScope(@NotNull GeneratorToolState state) {
    Preconditions.checkArgument(state.getMcModelPath() != null);
    IMontiThingsGlobalScope mtScope = MontiThingsMill.globalScope();
    mtScope.clear();
    mtScope.setModelPath(state.getMcModelPath());
    mtScope.setFileExt(state.getTool().getMTFileExtension());
    state.getTool().addBasicTypes();
    addAllLibraryFunctions(mtScope);
    return mtScope;
  }

}
