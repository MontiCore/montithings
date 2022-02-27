// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.symbolTable;

import de.monticore.cd4code._symboltable.CD4CodeGlobalScope;
import de.monticore.cd4code._symboltable.ICD4CodeScope;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.MontiThingsGlobalScope;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.cd2cpp.CppGenerator;
import montithings.generator.config.PortNameTrafo;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CreateCoComponents extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    CD4CodeGlobalScope componentTypeScopes = state.getTool().createClassDiagrams(
      (MontiThingsGlobalScope) state.getSymTab(), state.getSymbolPath());
    if (state.getModels().getClassdiagrams().isEmpty()) {
      state.getMcModelPath().addEntry(Paths.get(state.getSymbolPath()));
    }
    state.getTool().createSymbolTable(state.getSymTab());
    // ports introduced by the ComponentTypePortsNamingTrafo have to be added in class diagrams
    if (state.getConfig().getPortNameTrafo() == PortNameTrafo.ON) {
      componentTypeScopes = state.getTool().createMissingClassDiagrams(
        (MontiThingsGlobalScope) state.getSymTab(), state.getSymbolPath());
    }

    // generate here, as CD4CodeGlobalScope is reset by CDLangExtension symbol table
    generateComponentTypeCDs(componentTypeScopes, state.getTarget());
  }

  protected void generateComponentTypeCDs(CD4CodeGlobalScope scopes, File targetFilepath) {
    for (ICD4CodeScope scope : scopes.getSubScopes()) {
      String modelName = scope.getName();
      Log.info("Generate ComponentType model: " + modelName, MontiThingsGeneratorTool.TOOL_NAME);
      Path outDir = Paths.get(targetFilepath.getAbsolutePath());
      new CppGenerator(outDir, scope)
        .generate(Optional.empty());
    }
  }

}
