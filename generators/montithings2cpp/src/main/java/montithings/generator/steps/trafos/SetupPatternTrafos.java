package montithings.generator.steps.trafos;

import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.config.ApplyPatterns;
import montithings.generator.config.ConfigParams;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import montithings.trafos.patterns.AnomalyDetectionPatternTrafo;

public class SetupPatternTrafos extends GeneratorStep {

    @Override
    public void action(GeneratorToolState state) {
        ConfigParams config = state.getConfig();
        MontiThingsGeneratorTool tool = state.getTool();

        int windowSize = 25;
        double tolerance = 5;

        if (config.getApplyPatterns() == ApplyPatterns.ON) {
            tool.addTrafo(new AnomalyDetectionPatternTrafo(state.getModelPath(), windowSize, tolerance));
        }
    }
}
