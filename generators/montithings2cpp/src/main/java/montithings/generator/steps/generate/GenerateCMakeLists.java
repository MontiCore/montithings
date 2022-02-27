// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import montithings.generator.config.SplittingMode;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

public class GenerateCMakeLists extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    if (state.getConfig().getSplittingMode() != SplittingMode.OFF) {
      state.getMtg().generateMakeFileForSubdirs(
        state.getTarget(),
        state.getExecutableSubdirs(),
        state.getExecutableSensorActuatorPorts(),
        state.getConfig()
      );
    }
  }

}
