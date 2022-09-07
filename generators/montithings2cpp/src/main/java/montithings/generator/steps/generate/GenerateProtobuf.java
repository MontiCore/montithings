// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.se_rwth.commons.logging.Log;
import montiarc.util.Modelfinder;
import montithings.generator.cd2proto.ParseException;
import montithings.generator.cd2proto.ProtoGenerator;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

public class GenerateProtobuf extends GeneratorStep {
  @Override
  public void action(GeneratorToolState state) {
    List<String> foundModels = Modelfinder.getModelsInModelPath(state.getModelPath(), CD4AnalysisGlobalScope.EXTENSION);
    Log.info("Generating Protocol Buffer files for " + foundModels.size() + " models...", TOOL_NAME);
    for (String model : foundModels) {
      Log.info("Generate CD model protocol buffers: " + model, TOOL_NAME);
      Path outDir = Paths.get(state.getTarget().getAbsolutePath());
      ProtoGenerator generator = new ProtoGenerator(outDir, Paths.get(state.getModelPath().getAbsolutePath()), model);
      Set<Path> protoFiles;
      try {
        protoFiles = generator.generate();
        state.setProtoFiles(protoFiles);
      } catch (IOException | ParseException e) {
        e.printStackTrace();
        Log.error("0xMT1337 Could not generate Protocol Buffer description file for \"" + model + "\":", e);
      }
    }
  }
}
