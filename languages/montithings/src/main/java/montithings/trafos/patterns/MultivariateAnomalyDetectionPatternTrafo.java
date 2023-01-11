package montithings.trafos.patterns;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;

import java.util.Collection;

public class MultivariateAnomalyDetectionPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "MultivariateAnomalyDetectionPatternTrafo";

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation: Delayed Channels: " + targetComp.getComponentType().getName(),
                TOOL_NAME);

        return originalModels;
    }
}
