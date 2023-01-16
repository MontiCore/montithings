package montithings.trafos.patterns;

import arcbasis._ast.ASTPortAccess;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindConnectionsVisitor;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.*;

public class AnomalyDetectionPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
    private static final String TOOL_NAME = "AnomalyDetectionPatternTrafo";
    private final int windowSize;
    private final double tolerance;
    private final File modelPath;

    public AnomalyDetectionPatternTrafo(File modelPath, int windowSize, double tolerance) {
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

        Map<ASTPortAccess, List<Map<ASTPortAccess, ASTMCType>>> targetsToSourcePortType = this.getTargetsToSourcePortType(targetComp, allModels);

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

                    List<ASTMACompilationUnit> interceptComponents = this.getInterceptComponents(sources, target, targetComp, allModels, portType);

                    additionalTrafoModels.addAll(interceptComponents);
                }

                if (count == 1) {
                    List<ASTPortAccess> sources = getSourcesOfPortType(portType, sourcesToPortTypes);

                    ASTPortAccess source = sources.get(0);

                    System.out.println("Connection " + source.getQName() + " -> " + target.getQName() +
                            " is univariate numeric port. Intercept a new Autoregressive Anomaly Detection component.");

                    List<ASTMACompilationUnit> interceptComponents = this.getInterceptComponents(sources, target, targetComp, allModels, portType);

                    additionalTrafoModels.addAll(interceptComponents);
                }
            }
        }

        return additionalTrafoModels;
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


        if (targetToSourcePortType.containsKey(target)) {
            List<Map<ASTPortAccess, ASTMCType>> sourcesToPortTypes = targetToSourcePortType.get(target);
            sourcesToPortTypes.add(sourceToPortType);
            targetToSourcePortType.put(target, sourcesToPortTypes);
        } else {
            List<Map<ASTPortAccess, ASTMCType>> sourcesToPortTypes = Collections.singletonList(sourceToPortType);
            targetToSourcePortType.put(target, sourcesToPortTypes);
        }
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

    private boolean isNumericPort(ASTMCType portType) {
        List<String> numericPortTypes = Arrays.asList("int", "double", "float");
        return numericPortTypes.contains(portType.toString());
    }

    private List<ASTMACompilationUnit> getAllModels(Collection<ASTMACompilationUnit> originalModels,
                                                    Collection<ASTMACompilationUnit> addedModels) {
        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);
        return allModels;
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

    private List<ASTMACompilationUnit> getInterceptComponents(List<ASTPortAccess> sources, ASTPortAccess target, ASTMACompilationUnit comp,
                                                              List<ASTMACompilationUnit> models, ASTMCType portType) {
        List<ASTMACompilationUnit> interceptorComponents = new ArrayList<>();

        String interceptorComponentName = this.getInterceptorComponentName(sources, target, comp, models);

        ASTMCQualifiedName fullyQName = this.getInterceptorFullyQName(interceptorComponentName, comp);

        addSubComponentInstantiation(comp, fullyQName, interceptorComponentName.toLowerCase(), createEmptyArguments());

        ASTMACompilationUnit interceptorComponent = createCompilationUnit(comp.getPackage(), interceptorComponentName);


        // Behaviour via HWC Cpp class

        flagAsGenerated(interceptorComponent);

        // Replace the old connections
        for (ASTPortAccess source : sources) {
            String inPortName = "in" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(source.getQName()));
            String outPortName = "out" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(source.getQName()));

            addPort(interceptorComponent, inPortName, false, portType);
            addPort(interceptorComponent, outPortName, true, portType);

            removeConnection(comp, source, target);

            addConnection(comp, source.getQName(), interceptorComponentName.toLowerCase() + "." + inPortName);
            addConnection(comp, interceptorComponentName.toLowerCase() + "." + outPortName, target.getQName());
        }

        interceptorComponents.add(interceptorComponent);

        return interceptorComponents;
    }

    private String getInterceptorComponentName(List<ASTPortAccess> sources, ASTPortAccess target, ASTMACompilationUnit comp, List<ASTMACompilationUnit> models) {
        String newNameEnding = "AutoregressiveAnomalyDetection";
        String newName = newNameEnding;

        List<String> qCompSourceNames = new ArrayList<>();
        List<String> qCompTargetNames = new ArrayList<>();

        for (ASTPortAccess source : sources) {
            if (source.isPresentComponent()) {
                qCompSourceNames.addAll(TrafoUtil.getFullyQInstanceName(models, comp, source.getComponent()));
            }

            if (target.isPresentComponent()) {
                qCompTargetNames.addAll(TrafoUtil.getFullyQInstanceName(models, comp, target.getComponent()));
            }
        }

        for (String qCompSourceName : qCompSourceNames) {
            for (String qCompTargetName : qCompTargetNames) {
                newName = TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) +
                        TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) +
                        newNameEnding);
            }
        }

        return newName;
    }

    private ASTMCQualifiedName getInterceptorFullyQName(String interceptorComponentName, ASTMACompilationUnit comp) {
        ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(comp.getPackage());
        fullyQName.addParts(interceptorComponentName);

        return fullyQName;
    }
}
