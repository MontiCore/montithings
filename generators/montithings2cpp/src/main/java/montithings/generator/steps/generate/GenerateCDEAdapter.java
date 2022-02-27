// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import montithings.generator.config.SplittingMode;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

public class GenerateCDEAdapter extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    if (state.getConfig().getSplittingMode() == SplittingMode.OFF) {
      montithings.generator.steps.helper.GenerateCDEAdapter.generateCDEAdapter(state.getTarget(),
        state);
    }
  }

}
