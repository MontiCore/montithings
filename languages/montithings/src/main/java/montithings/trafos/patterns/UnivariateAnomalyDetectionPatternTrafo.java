package montithings.trafos.patterns;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindConnectionsVisitor;
import montithings.trafos.MontiThingsTrafo;

import java.io.File;
import java.util.*;

public class UnivariateAnomalyDetectionPatternTrafo extends PatternHelper implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "UnivariateAnomalyDetectionPatternTrafo";
    private final int windowSize;
    private final double tolerance;
    private final File modelPath;

    public UnivariateAnomalyDetectionPatternTrafo(File modelPath,int windowSize, double tolerance) {
        this.windowSize = windowSize;
        this.tolerance = tolerance;
        this.modelPath = modelPath;
    }

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation to: " + targetComp.getComponentType().getName(),
                TOOL_NAME);

        List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        // 1. Get all connections of comp
        List<FindConnectionsVisitor.Connection> connections = this.getConnections(targetComp);

        for (FindConnectionsVisitor.Connection conn : connections) {
            // 2. Check connection type i.e. which data is sent there
            ASTMCType portType = this.getPortType(conn.source, targetComp, allModels, this.modelPath);

            // 3. If data is numeric Intercept a new Autoregressive Anomaly Detection component
            if (isNumericPort(portType)) {
                System.out.println("Connection " + conn.source.getQName() + " -> " + conn.target.getQName() +
                        " is numeric port. Intercept a new Autoregressive Anomaly Detection component.");

                ASTMACompilationUnit interceptComponent = this.getInterceptComponent(conn.source, conn.target, targetComp, allModels, portType);

                additionalTrafoModels.add(interceptComponent);
            }
        }

        return additionalTrafoModels;
    }
}
