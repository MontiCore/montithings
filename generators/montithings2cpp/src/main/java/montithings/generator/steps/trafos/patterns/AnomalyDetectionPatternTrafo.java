package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTPortAccess;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindConnectionsVisitor;
import montithings.generator.data.GeneratorToolState;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.TrafoUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AnomalyDetectionPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
    private static final String TOOL_NAME = "AnomalyDetectionPatternTrafo";
    private static final String UNIVARIATE_IMPL_CPP = "template/patterns/AutoregressiveAnomalyDetectionImplCpp.ftl";
    private static final String UNIVARIATE_IMPL_HEADER = "template/patterns/AutoregressiveAnomalyDetectionImplHeader.ftl";
    private static final String UNIVARIATE_STATE_CPP = "template/patterns/AutoregressiveAnomalyDetectionStateCpp.ftl";
    private static final String UNIVARIATE_STATE_HEADER = "template/patterns/AutoregressiveAnomalyDetectionStateHeader.ftl";
    private static final String MULTIVARIATE_IMPL_CPP = "template/patterns/MultivariateAutoregressiveAnomalyDetectionImplCpp.ftl";
    private static final String MULTIVARIATE_IMPL_HEADER = "template/patterns/MultivariateAutoregressiveAnomalyDetectionImplHeader.ftl";
    private static final String MULTIVARIATE_STATE_CPP = "template/patterns/MultivariateAutoregressiveAnomalyDetectionStateCpp.ftl";
    private static final String MULTIVARIATE_STATE_HEADER = "template/patterns/MultivariateAutoregressiveAnomalyDetectionStateHeader.ftl";
    private static final String UNIVARIATE_NAME = "UniAutoregressiveAnomalyDetection";
    private static final String MULTIVARIATE_NAME = "MultiAutoregressiveAnomalyDetection";
    private static final String INPUT_PORT = "in";
    private static final String OUTPUT_PORT = "out";
    private static final String INPORT_NAME_KEY = "inPortNames";
    private static final String OUTPORT_NAME_KEY = "outPortNames";
    private static final List<String> NUMERIC_PORTS = Arrays.asList("int", "double", "float");
    private final File modelPath;
    private final int windowSize;
    private final double tolerance;
    private final File targetHwcPath;
    private final File srcHwcPath;
    private ASTMACompilationUnit multivariateComp;
    private ASTMACompilationUnit univariateComp;

    public AnomalyDetectionPatternTrafo(GeneratorToolState state, int windowSize, double tolerance) {
        this.windowSize = windowSize;
        this.tolerance = tolerance;
        this.modelPath = state.getModelPath();
        this.srcHwcPath = state.getHwcPath();
        this.targetHwcPath = Paths.get(state.getTarget().getAbsolutePath(), "hwc").toFile();
    }

    @Override
    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation to: " + targetComp.getComponentType().getName(), TOOL_NAME);

        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

        Map<ASTPortAccess, Map<ASTMCType, List<ASTPortAccess>>> targetsToPortTypesToSources =
                this.getTargetsToPortTypesToSources(targetComp, allModels);

        List<String> univariateInPortNames = new ArrayList<>();
        List<String> univariateOutPortNames = new ArrayList<>();
        List<List<String>> multivariateInPortNames = new ArrayList<>();
        List<List<String>> multivariateOutPortNames = new ArrayList<>();

        // For each target check count of sources with same port type
        // If == 1, insert UnivariateAnomalyDetection component
        // If > 1, insert MultivariateAnomalyDetection component
        for (Map.Entry<ASTPortAccess, Map<ASTMCType, List<ASTPortAccess>>> targetToPortTypesToSources : targetsToPortTypesToSources.entrySet()) {
            ASTPortAccess target = targetToPortTypesToSources.getKey();
            Map<ASTMCType, List<ASTPortAccess>> portTypesToSources = targetToPortTypesToSources.getValue();

            for (Map.Entry<ASTMCType, List<ASTPortAccess>> portTypeToSources : portTypesToSources.entrySet()) {
                ASTMCType portType = portTypeToSources.getKey();
                List<ASTPortAccess> sources = portTypeToSources.getValue();
                int count = portTypeToSources.getValue().size();

                if (count > 1) {
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
                            this.replaceConnection(sources, targetComp, portType, multivariateComp, MULTIVARIATE_NAME);

                    multivariateInPortNames.add(portNames.get(INPORT_NAME_KEY));
                    multivariateOutPortNames.add(portNames.get(OUTPORT_NAME_KEY));
                }

                if (count == 1) {
                    ASTPortAccess source = sources.get(0);

                    Log.info("Connection " + source.getQName() + " -> " + target.getQName() +
                            " is univariate numeric port. Intercept a new Autoregressive Anomaly Detection component.", TOOL_NAME);

                    if (univariateComp == null) {
                        univariateComp = this.getInterceptComponent(UNIVARIATE_NAME, targetComp);
                        additionalTrafoModels.add(univariateComp);
                        allModels.add(univariateComp);
                    }

                    Map<String, List<String>> portNames =
                            this.replaceConnection(sources, targetComp, portType, univariateComp, UNIVARIATE_NAME);

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

        Log.info("Return " + additionalTrafoModels.size() + " additional trafo models", TOOL_NAME);
        return additionalTrafoModels;
    }

    private Map<ASTPortAccess, Map<ASTMCType, List<ASTPortAccess>>> getTargetsToPortTypesToSources(ASTMACompilationUnit comp,
                                                                                                   List<ASTMACompilationUnit> models) throws Exception {
        Map<ASTPortAccess, Map<ASTMCType, List<ASTPortAccess>>> targetsToPortTypesToSources = new HashMap<>();

        List<FindConnectionsVisitor.Connection> connections = this.getConnections(comp);

        for (FindConnectionsVisitor.Connection conn : connections) {
            ASTMCType portType = this.getPortType(conn.source, comp, models, this.modelPath);

            if (isNumericPort(portType)) {
                if (targetsToPortTypesToSources.entrySet().size() == 0) {
                    Map<ASTMCType, List<ASTPortAccess>> portTypeToSources = new HashMap<>();
                    portTypeToSources.put(portType, Collections.singletonList(conn.source));
                    targetsToPortTypesToSources.put(conn.target, portTypeToSources);
                    Log.info("Init empty for target " + conn.target.getComponent(), TOOL_NAME);
                    continue;
                }

                ASTPortAccess foundKey = null;

                for (Map.Entry<ASTPortAccess, Map<ASTMCType, List<ASTPortAccess>>> entry : targetsToPortTypesToSources.entrySet()) {
                    if (entry.getKey().getComponent().equals(conn.target.getComponent())) {
                        Log.info("Found key " + entry.getKey().getComponent(), TOOL_NAME);
                        foundKey = entry.getKey();
                    }
                }

                Map<ASTMCType, List<ASTPortAccess>> portTypeToSources = new HashMap<>();

                if (foundKey != null) {
                    Log.info("Put " + foundKey.getComponent() + " with list len " + targetsToPortTypesToSources.get(foundKey).entrySet().size(), TOOL_NAME);
                    for (Map.Entry<ASTMCType, List<ASTPortAccess>> entry : targetsToPortTypesToSources.get(foundKey).entrySet()) {
                        if (entry.getKey().toString().equals(portType.toString())) {
                            List<ASTPortAccess> oldSources = new ArrayList<>(entry.getValue());
                            oldSources.add(conn.source);
                            portTypeToSources.put(entry.getKey(), oldSources);
                        } else {
                            portTypeToSources.put(entry.getKey(), entry.getValue());
                        }
                    }

                    targetsToPortTypesToSources.put(foundKey, portTypeToSources);
                } else {
                    Log.info("Target " + conn.target.getComponent() + " not found. Add empty", TOOL_NAME);
                    portTypeToSources.put(portType, Collections.singletonList(conn.source));
                    targetsToPortTypesToSources.put(conn.target, portTypeToSources);
                }
            }
        }

        return targetsToPortTypesToSources;
    }

    private Map<String, List<String>> replaceConnection(List<ASTPortAccess> sources, ASTMACompilationUnit comp, ASTMCType portType,
                                                        ASTMACompilationUnit interceptorComponent, String interceptorComponentName) {
        Log.info("Replace connection", TOOL_NAME);

        List<String> inPortNames = new ArrayList<>();
        List<String> outPortNames = new ArrayList<>();

        for (int i = 0; i < sources.size(); i++) {
            ASTPortAccess source = sources.get(i);
            ASTPortAccess target = this.getTargetOfSource(comp, source);

            if (target == null) {
                Log.info("No target for source " + source.getQName() + " found", TOOL_NAME);
                continue;
            }

            String inPortName = INPUT_PORT + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(source.getQName()));
            String outPortName = OUTPUT_PORT + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(target.getQName() + i));

            inPortNames.add(inPortName);
            outPortNames.add(outPortName);

            Log.info("Add new in-port " + inPortName + " of type " + portType + " to component " + interceptorComponentName, TOOL_NAME);
            addPort(interceptorComponent, inPortName, false, portType);

            Log.info("Add new out-port " + outPortName + " of type " + portType + " to component " + interceptorComponentName, TOOL_NAME);
            addPort(interceptorComponent, outPortName, true, portType);

            Log.info("Add new connection " + source.getQName() + " -> " + interceptorComponentName.toLowerCase() + "." + inPortName +
                    " to component " + comp.getComponentType(), TOOL_NAME);
            addConnection(comp, source.getQName(), interceptorComponentName.toLowerCase() + "." + inPortName);

            Log.info("Add new connection " + interceptorComponentName.toLowerCase() + "." + outPortName + " -> " + target.getQName() +
                    " to component " + comp.getComponentType(), TOOL_NAME);
            addConnection(comp, interceptorComponentName.toLowerCase() + "." + outPortName, target.getQName());

            Log.info("Remove existing connection " + source.getQName() + " -> " + target.getQName() +
                    " from component " + comp.getComponentType(), TOOL_NAME);
            removeConnection(comp, source, target);
        }

        Map<String, List<String>> portNames = new HashMap<>();
        portNames.put(INPORT_NAME_KEY, inPortNames);
        portNames.put(OUTPORT_NAME_KEY, outPortNames);

        return portNames;
    }

    private ASTPortAccess getTargetOfSource(ASTMACompilationUnit comp, ASTPortAccess source) {
        List<FindConnectionsVisitor.Connection> connections = this.getConnections(comp);

        for (FindConnectionsVisitor.Connection conn : connections) {
            if (conn.source.getQName().equals(source.getQName())) {
                return conn.target;
            }
        }

        return null;
    }

    private void generateUnivariateAnomalyDetectionBehavior(ASTMACompilationUnit outermostComponent,
                                                            List<String> inputPortNames,
                                                            List<String> outputPortNames) {
        Log.info("Generate univariate behavior with " + inputPortNames.size() + " input ports and " +
                outputPortNames.size() + " output ports.", TOOL_NAME);

        File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), outermostComponent.getPackage().getQName()).toFile();
        File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), outermostComponent.getPackage().getQName()).toFile();

        this.generate(tHwcPath, UNIVARIATE_NAME + "Impl", ".cpp", UNIVARIATE_IMPL_CPP,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, inputPortNames, outputPortNames);

        this.generate(sHwcPath, UNIVARIATE_NAME + "Impl", ".cpp", UNIVARIATE_IMPL_CPP,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, inputPortNames, outputPortNames);

        this.generate(tHwcPath, UNIVARIATE_NAME + "Impl", ".h", UNIVARIATE_IMPL_HEADER,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, windowSize, tolerance);

        this.generate(sHwcPath, UNIVARIATE_NAME + "Impl", ".h", UNIVARIATE_IMPL_HEADER,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, windowSize, tolerance);

        this.generate(tHwcPath, UNIVARIATE_NAME + "State", ".cpp", UNIVARIATE_STATE_CPP,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, inputPortNames);

        this.generate(sHwcPath, UNIVARIATE_NAME + "State", ".cpp", UNIVARIATE_STATE_CPP,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, inputPortNames);

        this.generate(tHwcPath, UNIVARIATE_NAME + "State", ".h", UNIVARIATE_STATE_HEADER,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, inputPortNames);

        this.generate(sHwcPath, UNIVARIATE_NAME + "State", ".h", UNIVARIATE_STATE_HEADER,
                outermostComponent.getPackage().getQName(), UNIVARIATE_NAME, inputPortNames);
    }

    private void generateMultivariateAnomalyDetectionBehavior(ASTMACompilationUnit outermostComponent,
                                                              List<List<String>> inputPortNames,
                                                              List<List<String>> outputPortNames) {
        Log.info("Generate multivariate behavior with " + inputPortNames.size() + " input port batches and " +
                outputPortNames.size() + " output port batches.", TOOL_NAME);

        File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), outermostComponent.getPackage().getQName()).toFile();
        File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), outermostComponent.getPackage().getQName()).toFile();

        this.generate(tHwcPath, MULTIVARIATE_NAME + "Impl", ".cpp", MULTIVARIATE_IMPL_CPP,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, inputPortNames, outputPortNames);

        this.generate(sHwcPath, MULTIVARIATE_NAME + "Impl", ".cpp", MULTIVARIATE_IMPL_CPP,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, inputPortNames, outputPortNames);

        this.generate(tHwcPath, MULTIVARIATE_NAME + "Impl", ".h", MULTIVARIATE_IMPL_HEADER,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, windowSize, tolerance);

        this.generate(sHwcPath, MULTIVARIATE_NAME + "Impl", ".h", MULTIVARIATE_IMPL_HEADER,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, windowSize, tolerance);

        this.generate(tHwcPath, MULTIVARIATE_NAME + "State", ".cpp", MULTIVARIATE_STATE_CPP,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, inputPortNames);

        this.generate(sHwcPath, MULTIVARIATE_NAME + "State", ".cpp", MULTIVARIATE_STATE_CPP,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, inputPortNames);

        this.generate(tHwcPath, MULTIVARIATE_NAME + "State", ".h", MULTIVARIATE_STATE_HEADER,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, inputPortNames);

        this.generate(sHwcPath, MULTIVARIATE_NAME + "State", ".h", MULTIVARIATE_STATE_HEADER,
                outermostComponent.getPackage().getQName(), MULTIVARIATE_NAME, inputPortNames);
    }

    private void generate(File target, String name, String fileExtension, String template, Object... templateArguments) {
        Path path = Paths.get(target.getAbsolutePath() + File.separator + name + fileExtension);
        Log.debug("Writing to file " + path, "FileGenerator");

        GeneratorSetup setup = new GeneratorSetup();
        setup.setTracing(false);
        GeneratorEngine engine = new GeneratorEngine(setup);
        engine.generateNoA(template, path, templateArguments);
    }

    private boolean isNumericPort(ASTMCType portType) {
        return NUMERIC_PORTS.contains(portType.toString());
    }

}
