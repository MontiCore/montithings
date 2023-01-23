package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTPortAccess;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._visitor.FindConnectionsVisitor;
import montithings.generator.codegen.FileGenerator;
import montithings.generator.data.GeneratorToolState;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.TrafoUtil;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class AnomalyDetectionPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
    private static final String TOOL_NAME = "AnomalyDetectionPatternTrafo";
    private static final String UNIVARIATE_IMPL = "template/patterns/AutoregressiveAnomalyDetectionImplCpp.ftl";
    private static final String UNIVARIATE_HEADER = "template/patterns/AutoregressiveAnomalyDetectionImplHeader.ftl";
    private static final String MULTIVARIATE_IMPL = "template/patterns/MultivariateAutoregressiveAnomalyDetectionImplCpp.ftl";
    private static final String MULTIVARIATE_HEADER = "template/patterns/MultivariateAutoregressiveAnomalyDetectionImplHeader.ftl";
    private static final String UNIVARIATE_NAME = "UniAutoregressiveAnomalyDetection";
    private static final String MULTIVARIATE_NAME = "MultiAutoregressiveAnomalyDetection";
    private static final String INPUT_PORT = "in";
    private static final String OUTPUT_PORT = "out";
    private static final String LOCAL_STATE_VAR = "values";
    private static final String INPORT_NAME_KEY = "inPortNames";
    private static final String OUTPORT_NAME_KEY = "outPortNames";
    private static final List<String> NUMERIC_PORTS = Arrays.asList("int", "double", "float");
    private final File modelPath;
    private final int windowSize;
    private final double tolerance;
    private final File hwcPath;
    private final FileGenerator fg;
    private ASTMACompilationUnit multivariateComp;
    private ASTMACompilationUnit univariateComp;

    public AnomalyDetectionPatternTrafo(GeneratorToolState state, int windowSize, double tolerance) {
        this.windowSize = windowSize;
        this.tolerance = tolerance;
        this.modelPath = state.getModelPath();
        this.hwcPath = state.getHwcPath();
        this.fg = new FileGenerator(state.getHwcPath(), state.getHwcPath());
    }

    @Override
    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation to: " + targetComp.getComponentType().getName(), TOOL_NAME);

        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

        Map<ASTPortAccess, List<Map<ASTPortAccess, ASTMCType>>> targetsToSourcePortType = this.getTargetsToSourcePortType(targetComp, allModels);

        List<String> univariateInPortNames = new ArrayList<>();
        List<String> univariateOutPortNames = new ArrayList<>();
        List<List<String>> multivariateInPortNames = new ArrayList<>();
        List<List<String>> multivariateOutPortNames = new ArrayList<>();

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

                    Log.info(logMessage.toString(), TOOL_NAME);

                    if (multivariateComp == null) {
                        multivariateComp = this.getInterceptComponent(MULTIVARIATE_NAME, targetComp);
                        additionalTrafoModels.add(multivariateComp);
                        allModels.add(multivariateComp);
                    }

                    Map<String, List<String>> portNames =
                            this.replaceConnection(sources, target, targetComp, portType, multivariateComp, MULTIVARIATE_NAME);

                    multivariateInPortNames.add(portNames.get(INPORT_NAME_KEY));
                    multivariateOutPortNames.add(portNames.get(OUTPORT_NAME_KEY));
                }

                if (count == 1) {
                    List<ASTPortAccess> sources = getSourcesOfPortType(portType, sourcesToPortTypes);

                    ASTPortAccess source = sources.get(0);

                    Log.info("Connection " + source.getQName() + " -> " + target.getQName() +
                            " is univariate numeric port. Intercept a new Autoregressive Anomaly Detection component.", TOOL_NAME);

                    if (univariateComp == null) {
                        univariateComp = this.getInterceptComponent(UNIVARIATE_NAME, targetComp);
                        additionalTrafoModels.add(univariateComp);
                        allModels.add(univariateComp);
                    }

                    Map<String, List<String>> portNames =
                            this.replaceConnection(sources, target, targetComp, portType, univariateComp, UNIVARIATE_NAME);

                    univariateInPortNames.add(portNames.get(INPORT_NAME_KEY).get(0));
                    univariateOutPortNames.add(portNames.get(OUTPORT_NAME_KEY).get(0));
                }
            }
        }

        if (univariateInPortNames.size() > 0 && univariateInPortNames.size() == univariateOutPortNames.size()) {
            this.generateUnivariateAnomalyDetectionBehavior(targetComp, univariateInPortNames, univariateOutPortNames);
        }

        if (multivariateInPortNames.size() > 0 && multivariateInPortNames.size() == multivariateOutPortNames.size()) {
            this.generateMultivariateAnomalyDetectionBehavior(targetComp, multivariateInPortNames, multivariateOutPortNames);
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

                if (portTypeToCount.entrySet().size() == 0) {
                    portTypeToCount.put(portType, 1);
                    continue;
                }

                for (Map.Entry<ASTMCType, Integer> count : portTypeToCount.entrySet()) {
                    if (count.getKey().equals(portType)) {
                        int curCount = count.getValue();
                        portTypeToCount.put(portType, curCount + 1);
                    } else {
                        portTypeToCount.put(portType, 1);
                    }
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
                Map<ASTPortAccess, ASTMCType> sourceToPortType = new HashMap<>();
                sourceToPortType.put(conn.source, portType);

                if (targetsToSourcePortType.entrySet().size() == 0) {
                    targetsToSourcePortType.put(conn.target, Collections.singletonList(sourceToPortType));
                    continue;
                }

                ASTPortAccess foundKey = null;

                for (Map.Entry<ASTPortAccess, List<Map<ASTPortAccess, ASTMCType>>> entry : targetsToSourcePortType.entrySet()) {
                    if (entry.getKey().getQName().equals(conn.target.getQName())) {
                        foundKey = entry.getKey();
                    }
                }

                if (foundKey != null) {
                    List<Map<ASTPortAccess, ASTMCType>> sourcesToPortType = new ArrayList<>(Collections.singletonList(sourceToPortType));
                    sourcesToPortType.addAll(targetsToSourcePortType.get(foundKey));
                    targetsToSourcePortType.put(foundKey, sourcesToPortType);
                } else {
                    targetsToSourcePortType.put(conn.target, Collections.singletonList(sourceToPortType));
                }
            }
        }

        return targetsToSourcePortType;
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

    private Map<String, List<String>> replaceConnection(List<ASTPortAccess> sources, ASTPortAccess target, ASTMACompilationUnit comp,
                                                        ASTMCType portType, ASTMACompilationUnit interceptorComponent, String interceptorComponentName) {
        Log.info("Replace connection", TOOL_NAME);

        List<String> inPortNames = new ArrayList<>();
        List<String> outPortNames = new ArrayList<>();

        for (ASTPortAccess source : sources) {
            String inPortName = INPUT_PORT + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(source.getQName()));
            String outPortName = OUTPUT_PORT + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(target.getQName()));

            inPortNames.add(inPortName);
            outPortNames.add(outPortName);

            addPort(interceptorComponent, inPortName, false, portType);
            addPort(interceptorComponent, outPortName, true, portType);

            addConnection(comp, source.getQName(), interceptorComponentName.toLowerCase() + "." + inPortName);
            addConnection(comp, interceptorComponentName.toLowerCase() + "." + outPortName, target.getQName());

            removeConnection(comp, source, target);
            // TODO: Add local state var
        }

        Map<String, List<String>> portNames = new HashMap<>();
        portNames.put(INPORT_NAME_KEY, inPortNames);
        portNames.put(OUTPORT_NAME_KEY, outPortNames);

        return portNames;
    }

    private ASTMACompilationUnit getInterceptComponent(String interceptorComponentName, ASTMACompilationUnit outermostComponent) {
        Log.info("Generate intercept anomaly detection component: " + interceptorComponentName, TOOL_NAME);

        ASTMCQualifiedName fullyQName = this.getInterceptorFullyQName(interceptorComponentName, outermostComponent.getPackage().getQName());

        addSubComponentInstantiation(outermostComponent, fullyQName, interceptorComponentName.toLowerCase(), createEmptyArguments());

        ASTMACompilationUnit interceptorComponent = createCompilationUnit(outermostComponent.getPackage(), interceptorComponentName);

        flagAsGenerated(interceptorComponent);

        return interceptorComponent;
    }

    private void generateUnivariateAnomalyDetectionBehavior(ASTMACompilationUnit outermostComponent,
                                                            List<String> inputPortNames,
                                                            List<String> outputPortNames) {
        Log.info("Generate univariate behavior with " + inputPortNames.size() + "input ports and " +
                outputPortNames.size() + "output ports.", TOOL_NAME);

        File targetPath = Paths.get(hwcPath.getAbsolutePath(), outermostComponent.getPackage().getQName()).toFile();

        fg.generate(targetPath, UNIVARIATE_NAME, ".cpp", UNIVARIATE_IMPL,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, inputPortNames, outputPortNames);

        fg.generate(targetPath, UNIVARIATE_NAME, ".h", UNIVARIATE_HEADER,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, tolerance, windowSize);
    }

    private void generateMultivariateAnomalyDetectionBehavior(ASTMACompilationUnit outermostComponent,
                                                              List<List<String>> inputPortNames,
                                                              List<List<String>> outputPortNames) {
        Log.info("Generate multivariate behavior with " + inputPortNames.size() + "input port batches and " +
                outputPortNames.size() + "output port batches.", TOOL_NAME);

        File targetPath = Paths.get(hwcPath.getAbsolutePath(), outermostComponent.getPackage().getQName()).toFile();

        fg.generate(targetPath, MULTIVARIATE_NAME, ".cpp", MULTIVARIATE_IMPL,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, inputPortNames, outputPortNames);

        fg.generate(targetPath, MULTIVARIATE_NAME, ".h", MULTIVARIATE_HEADER,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, tolerance, windowSize);
    }

    private List<ASTMACompilationUnit> getAllModels(Collection<ASTMACompilationUnit> originalModels,
                                                    Collection<ASTMACompilationUnit> addedModels) {
        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);
        return allModels;
    }

    private ASTMCQualifiedName getInterceptorFullyQName(String interceptorComponentName, String outermostPackage) {
        return MontiThingsMill
                .mCQualifiedNameBuilder()
                .addParts(outermostPackage)
                .addParts(interceptorComponentName)
                .build();
    }

    private boolean isNumericPort(ASTMCType portType) {
        return NUMERIC_PORTS.contains(portType.toString());
    }

}
