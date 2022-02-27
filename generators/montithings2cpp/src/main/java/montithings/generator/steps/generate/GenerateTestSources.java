// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.config.SplittingMode;
import montithings.generator.config.TargetPlatform;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.steps.GeneratorStep;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static montithings.generator.helper.FileHelper.*;

public class GenerateTestSources extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    if (state.getTestPath() != null && !state.getTestPath().toString().equals("")) {
      if (state.getConfig().getSplittingMode() != SplittingMode.OFF) {
        Log.info("--------------------------------", "MontiThingsGeneratorTool");
        Log.info("Generate Test Sources", "MontiThingsGeneratorTool");
        Log.info("--------------------------------", "MontiThingsGeneratorTool");
        state.getConfig().setSplittingMode(SplittingMode.OFF);
        MontiThingsGeneratorTool testTool = new MontiThingsGeneratorTool();
        testTool.generate(state.getModelPath(), Paths
          .get(Paths.get(state.getTarget().getAbsolutePath()).getParent().toString(),
            "generated-test-sources")
          .toFile(), state.getHwcPath(), state.getTestPath(), state.getConfig());
      }
      else {
        for (String model : state.getModels().getMontithings()) {
          ComponentTypeSymbol comp = state.getTool().modelToSymbol(model, state.getSymTab());
          if (ComponentHelper.isApplication(comp, state.getConfig())) {
            generateTests(state, comp);
          }
        }
      }
    }
  }

  protected void generateTests(GeneratorToolState state, ComponentTypeSymbol comp) {
    if (state.getTestPath() != null && state.getTestPath() != null && comp != null) {
      /* ============================================================ */
      /* ====== Copy generated-sources to generated-test-sources ==== */
      /* ============================================================ */
      copyGeneratedToTarget(state.getTestPath());
      copyTestToTarget(state.getTestPath(), state.getTarget(), comp);
      if (ComponentHelper.isApplication(comp, state.getConfig())) {
        Path target = Paths.get(
          Paths.get(state.getTarget().getAbsolutePath()).getParent().toString(),
          "generated-test-sources");
        File libraryPath = Paths.get(target.toString(), "montithings-RTE").toFile();
        // Check for Subpackages
        File[] subPackagesPath = getSubPackagesPath(state.getModelPath().getAbsolutePath());

        // generate make file
        if (state.getConfig().getTargetPlatform() != TargetPlatform.ARDUINO) {
          // Arduino uses its own build system
          state.getMtg().generateTestMakeFile(target.toFile(), comp, libraryPath, subPackagesPath);
        }
      }
    }
  }

}
