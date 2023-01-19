package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTPortAccess;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindConnectionsVisitor;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.*;

public class AnomalyDetectionPatternTrafo extends PatternHelper implements MontiThingsTrafo {
    private static final String TOOL_NAME = "AnomalyDetectionPatternTrafo";
    private final File modelPath;

    public AnomalyDetectionPatternTrafo(File modelPath) {
        this.modelPath = modelPath;
    }

    @Override
    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation to: " + targetComp.getComponentType().getName(),
                TOOL_NAME);

        List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

        Map<ASTPortAccess, List<Map<ASTPortAccess, ASTMCType>>> targetsToSourcePortType = this.getTargetsToSourcePortType(targetComp, allModels);

        ASTMACompilationUnit outermostComponent = this.getOutermostComponent(allModels);
        ASTMACompilationUnit univariateComp = this.getUnivariateComponent(allModels, modelPath);
        ASTMACompilationUnit multivariateComp = this.getMultivariateComponent(allModels, modelPath);

        // For each target check count of sources with same port type
        // If == 1, insert UnivariateAnomalyDetection component
        // If > 1, insert MultivariateAnomalyDetection component
        for (Map.Entry<ASTPortAccess, List<Map<ASTPortAccess, ASTMCType>>> targetToSourcePortType : targetsToSourcePortType.entrySet()) {
            ASTPortAccess target = targetToSourcePortType.getKey();
            List<Map<ASTPortAccess, ASTMCType>> sourcesToPortTypes = targetToSourcePortType.getValue();
            Map<ASTMCType, Integer> portTypeToCount = this.getPortTypeCount(sourcesToPortTypes);

            for (Map.Entry<ASTMCType, Integer> portTypeCount : portTypeToCount.entrySet()) {
                ASTMCType portType = portTypeCount.getKey();
                int count = portTypeCount.getValue();

                if (count > 1) {
                    List<ASTPortAccess> sources = getSourcesOfPortType(portType, sourcesToPortTypes);

                    StringBuilder logMessage = new StringBuilder("Connection ");

                    for (ASTPortAccess source : sources) {
                        logMessage.append(source.getQName()).append(" -> ").append(target.getQName()).append(", ");
                    }

                    logMessage.append(" is multivariate numeric port. Intercept a new Multivariate Autoregressive Anomaly Detection component.");

                    System.out.println(logMessage);

                    this.replaceConnection(sources, target, targetComp, portType, multivariateComp, outermostComponent);
                }

                if (count == 1) {
                    List<ASTPortAccess> sources = getSourcesOfPortType(portType, sourcesToPortTypes);

                    ASTPortAccess source = sources.get(0);

                    System.out.println("Connection " + source.getQName() + " -> " + target.getQName() +
                            " is univariate numeric port. Intercept a new Autoregressive Anomaly Detection component.");

                    this.replaceConnection(sources, target, targetComp, portType, univariateComp, outermostComponent);
                }
            }
        }

        return originalModels;
    }

    private List<ASTPortAccess> getSourcesOfPortType(ASTMCType portType, List<Map<ASTPortAccess, ASTMCType>> sourcesToPortTypes) {
        List<ASTPortAccess> sources = new ArrayList<>();

        for (Map<ASTPortAccess, ASTMCType> sourceToPortType : sourcesToPortTypes) {
            for (Map.Entry<ASTPortAccess, ASTMCType> entry : sourceToPortType.entrySet()) {
                ASTMCType sourcePortType = entry.getValue();

                if (portType.equals(sourcePortType)) {
                    sources.add(entry.getKey());
                }
            }
        }

        return sources;
    }

    private Map<ASTMCType, Integer> getPortTypeCount(List<Map<ASTPortAccess, ASTMCType>> sourcesToPortTypes) {
        Map<ASTMCType, Integer> portTypeToCount = new HashMap<>();

        for (Map<ASTPortAccess, ASTMCType> sourceToPortType : sourcesToPortTypes) {
            for (Map.Entry<ASTPortAccess, ASTMCType> entry : sourceToPortType.entrySet()) {
                ASTMCType portType = entry.getValue();

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

    private Map<ASTPortAccess, List<Map<ASTPortAccess, ASTMCType>>> getTargetsToSourcePortType(ASTMACompilationUnit comp,
                                                                                               List<ASTMACompilationUnit> models) throws Exception {
        Map<ASTPortAccess, List<Map<ASTPortAccess, ASTMCType>>> targetsToSourcePortType = new HashMap<>();

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
                              Map<ASTPortAccess, List<Map<ASTPortAccess, ASTMCType>>> targetToSourcePortType) {
        Map<ASTPortAccess, ASTMCType> sourceToPortType = new HashMap<>();
        sourceToPortType.put(source, portType);


        List<Map<ASTPortAccess, ASTMCType>> sourcesToPortTypes;

        if (targetToSourcePortType.containsKey(target)) {
            sourcesToPortTypes = targetToSourcePortType.get(target);
            sourcesToPortTypes.add(sourceToPortType);
        } else {
            sourcesToPortTypes = Collections.singletonList(sourceToPortType);
        }

        targetToSourcePortType.put(target, sourcesToPortTypes);
    }

    /**
     * Returns all connections inside this component i.e. in basic-input-output only the example component
     * has subcomponents which are connected and thus one connection would be returned
     */
    private List<FindConnectionsVisitor.Connection> getConnections(ASTMACompilationUnit comp) {
        FindConnectionsVisitor visitor = new FindConnectionsVisitor();
        comp.accept(visitor.createTraverser());
        return visitor.getConnections();
    }

    private ASTMCType getPortType(ASTPortAccess port, ASTMACompilationUnit comp, List<ASTMACompilationUnit> models, File modelPath) throws Exception {
        String sourceTypeName = TrafoUtil.getPortOwningComponentType(comp, port);

        ASTMCType portType = null;

        try {
            String qName = TrafoUtil.getFullyQNameFromImports(modelPath, comp, sourceTypeName).getQName();
            ASTMACompilationUnit compSource = TrafoUtil.getComponentByName(models, qName);
            portType = TrafoUtil.getPortTypeByName(compSource, port.getPort());
        } catch (ClassNotFoundException e) {
            // portType will be null which is caught later on
        } catch (NoSuchElementException e) {
            // model was not found. it is probably a generic type. in this case search for the port within the interfaces
            if (TrafoUtil.isGeneric(comp, sourceTypeName)) {
                for (String iface : TrafoUtil.getInterfaces(comp, sourceTypeName)) {
                    ASTMACompilationUnit ifaceComp = TrafoUtil
                            .getComponentByName(models, comp.getPackage() + "." + iface);
                    try {
                        portType = TrafoUtil.getPortTypeByName(ifaceComp, port.getPort());
                    } catch (Exception e1) {
                        //ignore, check next iface
                    }
                }
            }
        }

        if (portType == null) {
            throw new NoSuchElementException(
                    "No such port instance found which is named " + port.getPort());
        }

        return portType;
    }

    private void replaceConnection(List<ASTPortAccess> sources, ASTPortAccess target, ASTMACompilationUnit comp,
                                   ASTMCType portType, ASTMACompilationUnit interceptorComponent, ASTMACompilationUnit outermostComponent) {
        String interceptorComponentName = this.getInterceptorFullyQName(UNIVARIATE_NAME, outermostComponent.getPackage().getQName()).getQName();

        for (ASTPortAccess source : sources) {
            String inPortName = "in" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(source.getQName()));
            String outPortName = "out" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(source.getQName()));

            addPort(interceptorComponent, inPortName, false, portType);
            addPort(interceptorComponent, outPortName, true, portType);

            removeConnection(comp, source, target);

            addConnection(comp, source.getQName(), interceptorComponentName.toLowerCase() + "." + inPortName);
            addConnection(comp, interceptorComponentName.toLowerCase() + "." + outPortName, target.getQName());
        }
    }
}
