package montithings.generator.steps.trafos;

import montithings.generator.MontiThingsGeneratorTool;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import montithings.trafos.patterns.UnivariateAnomalyDetectionPatternTrafo;

public class SetupPatternTrafos extends GeneratorStep {

    @Override public void action(GeneratorToolState state) {
        MontiThingsGeneratorTool tool = state.getTool();

        int windowSize = 25;
        double tolerance = 5;

        tool.addTrafo(new UnivariateAnomalyDetectionPatternTrafo(state.getModelPath(), windowSize, tolerance));
    }
}
