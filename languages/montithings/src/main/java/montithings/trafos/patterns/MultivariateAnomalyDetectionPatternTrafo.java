package montithings.trafos.patterns;

import arcbasis._ast.ASTPortAccess;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindConnectionsVisitor;
import montithings.trafos.MontiThingsTrafo;

import java.io.File;
import java.util.*;

public class MultivariateAnomalyDetectionPatternTrafo extends PatternHelper implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "MultivariateAnomalyDetectionPatternTrafo";
    private final int windowSize;
    private final double tolerance;
    private final File modelPath;

    public MultivariateAnomalyDetectionPatternTrafo(File modelPath, int windowSize, double tolerance) {
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

        Map<ASTPortAccess, List<Map<ASTPortAccess, String>>> targetsToSourcePortType = this.getTargetsToSourcePortType(targetComp, allModels);

        // For each target check if there is more than 1 source with same port type
        // If yes, insert MultivariateAnomalyDetection component
        for (Map.Entry<ASTPortAccess, List<Map<ASTPortAccess, String>>> targetToSourcePortType : targetsToSourcePortType.entrySet()) {
            ASTPortAccess target = targetToSourcePortType.getKey();
            List<Map<ASTPortAccess, String>> sourcesToPortTypes = targetToSourcePortType.getValue();
            Map<String, Integer> portTypeToCount = this.getPortTypeCount(sourcesToPortTypes);

            for (Map.Entry<String, Integer> portTypeCount : portTypeToCount.entrySet()) {
                String portType = portTypeCount.getKey();
                int count = portTypeCount.getValue();

                if (count > 1) {
                    List<ASTPortAccess> sources = getSourcesOfPortType(portType, sourcesToPortTypes);

                    StringBuilder logMessage = new StringBuilder("Connection ");

                    for (ASTPortAccess source : sources) {
                        logMessage.append(source.getQName()).append(" -> ").append(target.getQName()).append(", ");
                    }

                    logMessage.append(" is multivariate numeric port. Intercept a new Multivariate Autoregressive Anomaly Detection component.");

                    System.out.println(logMessage);

                    // TODO: Apply transformation here
                }
            }
        }

        return additionalTrafoModels;
    }

    private List<ASTPortAccess> getSourcesOfPortType(String portType, List<Map<ASTPortAccess, String>> sourcesToPortTypes) {
        List<ASTPortAccess> sources = new ArrayList<>();

        for (Map<ASTPortAccess, String> sourceToPortType : sourcesToPortTypes) {
            for (Map.Entry<ASTPortAccess, String> entry : sourceToPortType.entrySet()) {
                String sourcePortType = entry.getValue();

                if (portType.equals(sourcePortType)) {
                    sources.add(entry.getKey());
                }
            }
        }

        return sources;
    }

    private Map<String, Integer> getPortTypeCount(List<Map<ASTPortAccess, String>> sourcesToPortTypes) {
        Map<String, Integer> portTypeToCount = new HashMap<>();

        for (Map<ASTPortAccess, String> sourceToPortType : sourcesToPortTypes) {
            for (Map.Entry<ASTPortAccess, String> entry : sourceToPortType.entrySet()) {
                String portType = entry.getValue();

                if (portTypeToCount.containsKey(portType)) {
                    int count = portTypeToCount.get(portType);
                    portTypeToCount.put(portType, count + 1);
                } else {
                    portTypeToCount.put(portType, 1);
                }
            }
        }

        return portTypeToCount;
    }

    private Map<ASTPortAccess, List<Map<ASTPortAccess, String>>> getTargetsToSourcePortType(ASTMACompilationUnit comp,
                                                                                            List<ASTMACompilationUnit> models) throws Exception {
        Map<ASTPortAccess, List<Map<ASTPortAccess, String>>> targetsToSourcePortType = new HashMap<>();

        List<FindConnectionsVisitor.Connection> connections = this.getConnections(comp);

        for (FindConnectionsVisitor.Connection conn : connections) {
            ASTMCType portType = this.getPortType(conn.source, comp, models, this.modelPath);

            if (isNumericPort(portType)) {
                this.storeMapping(conn.source, conn.target, portType, targetsToSourcePortType);
            }
        }

        return targetsToSourcePortType;
    }

    private void storeMapping(ASTPortAccess source, ASTPortAccess target, ASTMCType portType,
                              Map<ASTPortAccess, List<Map<ASTPortAccess, String>>> targetToSourcePortType) {
        Map<ASTPortAccess, String> sourceToPortType = new HashMap<>();
        sourceToPortType.put(source, portType.toString());


        if (targetToSourcePortType.containsKey(target)) {
            List<Map<ASTPortAccess, String>> sourcesToPortTypes = targetToSourcePortType.get(target);
            sourcesToPortTypes.add(sourceToPortType);
            targetToSourcePortType.put(target, sourcesToPortTypes);
        } else {
            List<Map<ASTPortAccess, String>> sourcesToPortTypes = Collections.singletonList(sourceToPortType);
            targetToSourcePortType.put(target, sourcesToPortTypes);
        }
    }
}
