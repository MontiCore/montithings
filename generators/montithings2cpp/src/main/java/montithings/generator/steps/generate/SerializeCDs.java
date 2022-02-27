// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.CD4MTTool;
import montithings.generator.steps.GeneratorStep;

import java.io.File;
import java.nio.file.Paths;

public class SerializeCDs extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    String symbolPath = state.getTarget().toString() + File.separator + "symbols" + File.separator;

    // Ensure symbols folder exists
    File symbolsFolder = new File(symbolPath);
    if (!symbolsFolder.exists()) {
      if (!symbolsFolder.mkdirs()) {
        Log.error("0xMT1200 Could not create directory '" + symbolsFolder.getAbsolutePath() + "'");
      }
    }

    if (!state.getModels().getClassdiagrams().isEmpty()) {
      CD4MTTool.convertToSymFile(state.getModelPath(), state.getModels().getClassdiagrams(),
        symbolPath);
      state.getMcModelPath().addEntry(Paths.get(symbolPath));
    }
    state.setSymbolPath(symbolPath);
  }

}
