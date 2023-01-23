package montithings.generator.steps.trafos;

import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.config.ApplyPatterns;
import montithings.generator.config.ConfigParams;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import montithings.generator.steps.trafos.patterns.AnomalyDetectionPatternTrafo;
import montithings.generator.steps.trafos.patterns.SetupAnomalyDetectionPatternTrafo;

public class SetupPatternTrafos extends GeneratorStep {
    private static final int WINDOW_SIZE = 25;
    private static final double TOLERANCE = 5.0;

    @Override
    public void action(GeneratorToolState state) {
        ConfigParams config = state.getConfig();
        MontiThingsGeneratorTool tool = state.getTool();

        if (config.getApplyPatterns() == ApplyPatterns.ON) {
            if (state.shouldCreateUnivariateAnomalyDetection() || state.shouldCreateMultivariateAnomalyDetection()) {
                tool.addTrafo(new SetupAnomalyDetectionPatternTrafo(state, WINDOW_SIZE, TOLERANCE));
            }

            tool.addTrafo(new AnomalyDetectionPatternTrafo(state));
        }
    }
}
