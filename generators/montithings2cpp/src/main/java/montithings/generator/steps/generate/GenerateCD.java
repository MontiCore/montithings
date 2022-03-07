// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.se_rwth.commons.logging.Log;
import montiarc.util.Modelfinder;
import montithings.generator.cd2cpp.CppGenerator;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

public class GenerateCD extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    List<String> foundModels = Modelfinder
      .getModelsInModelPath(state.getModelPath(), CD4AnalysisGlobalScope.EXTENSION);
    for (String model : foundModels) {
      Log.info("Generate CD model: " + model, TOOL_NAME);
      Path outDir = Paths.get(state.getTarget().getAbsolutePath());
      new CppGenerator(outDir, Paths.get(state.getModelPath().getAbsolutePath()),
        Paths.get(state.getHwcPath().getAbsolutePath()), model)
        .generate(Optional.empty());
    }
  }

}
