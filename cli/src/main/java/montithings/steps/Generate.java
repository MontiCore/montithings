// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import montithings.CLIState;
import montithings.CLIStep;

public class Generate extends CLIStep {

  @Override public void action(CLIState state) {
    state.getTool().generate(
      state.getModelPath().getFullPathOfEntries().stream().findFirst().get().toFile(),
      state.getTargetDirectory(),
      state.getHwcPath(),
      state.getTestPath(),
      state.getMtcfg().configParams
    );
  }

}
