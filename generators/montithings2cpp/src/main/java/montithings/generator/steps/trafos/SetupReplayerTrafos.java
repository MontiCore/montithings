// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.trafos;

import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.config.ConfigParams;
import montithings.generator.config.ReplayMode;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import montithings.trafos.DelayedChannelTrafo;
import montithings.trafos.DelayedComputationTrafo;
import montithings.trafos.ExternalPortMockTrafo;

/**
 * Sets up trafos for mocking sensors, actuators and adding delay for replay scenarios
 */
public class SetupReplayerTrafos extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    ConfigParams config = state.getConfig();
    MontiThingsGeneratorTool tool = state.getTool();

    if (config.getReplayMode() == ReplayMode.ON) {
      tool.addTrafo(new ExternalPortMockTrafo(state.getModelPath(), config.getReplayDataFile(),
        config.getMainComponent()));
      tool.addTrafo(new DelayedChannelTrafo(state.getModelPath(), config.getReplayDataFile()));
      tool.addTrafo(new DelayedComputationTrafo(state.getModelPath(), config.getReplayDataFile()));
    }
  }

}
