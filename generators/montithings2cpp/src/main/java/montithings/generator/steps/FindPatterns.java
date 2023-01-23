package montithings.generator.steps;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.visitor.AnomalyDetectionPatternVisitor;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

public class FindPatterns extends GeneratorStep {

    @Override
    public void action(GeneratorToolState state) {
        Log.info("Looking for patterns", TOOL_NAME);

        for (String model : state.getModels().getMontithings()) {
            String qualifier = Names.getQualifier(model);
            String qualifiedModelName = qualifier + (qualifier.isEmpty() ? "" : ".")
                    + Names.getSimpleName(model);

            AnomalyDetectionPatternVisitor visitor = new AnomalyDetectionPatternVisitor();
            ComponentTypeSymbol comp = state.getSymTab().resolveComponentType(qualifiedModelName).get();
            comp.getAstNode().accept(visitor);

            state.setPortTypeToCount(visitor.getPortTypeToCount());
            state.setPortTypeToFreeIndicesFromPortTypeCount();
            state.setCreateUnivariateAnomalyDetection(visitor.createUnivariateAnomalyDetection());
            state.setCreateMultivariateAnomalyDetection(visitor.createMultivariateAnomalyDetection());
        }
    }
}
