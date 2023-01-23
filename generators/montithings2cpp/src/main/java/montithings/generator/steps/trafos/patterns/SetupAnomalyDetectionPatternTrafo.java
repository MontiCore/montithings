package montithings.generator.steps.trafos.patterns;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings.generator.codegen.FileGenerator;
import montithings.generator.data.GeneratorToolState;
import montithings.trafos.MontiThingsTrafo;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SetupAnomalyDetectionPatternTrafo extends PatternHelper implements MontiThingsTrafo {
    private static final String TOOL_NAME = "SetupAnomalyDetectionPatternTrafo";
    private static final String UNIVARIATE_IMPL = "hwc/ftl/AutoregressiveAnomalyDetectionImplCpp";
    private static final String UNIVARIATE_HEADER = "hwc/ftl/AutoregressiveAnomalyDetectionImplHeader";
    private static final String MULTIVARIATE_IMPL = "hwc/ftl/MultivariateAutoregressiveAnomalyDetectionImplCpp";
    private static final String MULTIVARIATE_HEADER = "hwc/ftl/MultivariateAutoregressiveAnomalyDetectionImplHeader";
    private final int windowSize;
    private final double tolerance;
    private final File modelPath;
    private final File hwcPath;
    private final FileGenerator fg;
    private final Map<String, Integer> portTypesToCount;

    public SetupAnomalyDetectionPatternTrafo(GeneratorToolState state, int windowSize, double tolerance) {
        this.windowSize = windowSize;
        this.tolerance = tolerance;
        this.modelPath = state.getModelPath();
        this.hwcPath = Paths.get(state.getTarget().getAbsolutePath(), "hwc").toFile();
        this.portTypesToCount = state.getPortTypeToCount();
        this.fg = new FileGenerator(state.getTarget(), this.hwcPath);
    }

    @Override
    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation to: " + targetComp.getComponentType().getName(),
                TOOL_NAME);

        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

        if (!this.isOutermostComponent(allModels, targetComp)) {
            return additionalTrafoModels;
        }

        // Generate univariate anomaly detection component, if visitor detected that we need one (this.hasUnivariateAnomalyDetection())
        // && We didn't generate one already (this.getUnivariateComponent(allModels, modelPath) == null)
        if (this.hasUnivariateAnomalyDetection() && this.getUnivariateComponent(allModels, modelPath) == null) {
            ASTMACompilationUnit univariateComp =
                    this.getInterceptComponent(UNIVARIATE_NAME, targetComp, UNIVARIATE_IMPL, UNIVARIATE_HEADER);
            additionalTrafoModels.add(univariateComp);
        }

        if (this.hasMultivariateAnomalyDetection() && this.getMultivariateComponent(allModels, modelPath) == null) {
            ASTMACompilationUnit multivariateComp =
                    this.getInterceptComponent(MULTIVARIATE_NAME, targetComp, MULTIVARIATE_IMPL, MULTIVARIATE_HEADER);
            additionalTrafoModels.add(multivariateComp);
        }

        return additionalTrafoModels;
    }

    private boolean hasUnivariateAnomalyDetection() {
        for (int count : portTypesToCount.values()) {
            if (count == 1) {
                return true;
            }
        }

        return false;
    }

    private boolean hasMultivariateAnomalyDetection() {
        for (int count : portTypesToCount.values()) {
            if (count > 1) {
                return true;
            }
        }

        return false;
    }

    private ASTMACompilationUnit getInterceptComponent(String interceptorComponentName, ASTMACompilationUnit outermostComponent,
                                                       String cppImplName, String headerImplName) {
        ASTMCQualifiedName fullyQName = this.getInterceptorFullyQName(interceptorComponentName, outermostComponent.getPackage().getQName());

        addSubComponentInstantiation(outermostComponent, fullyQName, interceptorComponentName.toLowerCase(), createEmptyArguments());

        ASTMACompilationUnit interceptorComponent = createCompilationUnit(outermostComponent.getPackage(), interceptorComponentName);

        for (Map.Entry<String, Integer> portTypeToCount : portTypesToCount.entrySet()) {
            String portTypeName = portTypeToCount.getKey();

            // Generate in/out ports for each port type <count> times
            for (int i = 0; i < portTypeToCount.getValue(); i++) {
                ASTMCType inPortType = MontiThingsMill
                        .mCQualifiedTypeBuilder()
                        .setMCQualifiedName(MontiThingsMill
                                .mCQualifiedNameBuilder()
                                .addParts(INPUT_PORT)
                                .addParts(portTypeName)
                                .addParts(Integer.valueOf(i).toString())
                                .build())
                        .build();

                ASTMCType outPortType = MontiThingsMill
                        .mCQualifiedTypeBuilder()
                        .setMCQualifiedName(MontiThingsMill
                                .mCQualifiedNameBuilder()
                                .addParts(OUTPUT_PORT)
                                .addParts(portTypeName)
                                .addParts(Integer.valueOf(i).toString())
                                .build())
                        .build();

                addPort(interceptorComponent, INPUT_PORT + portTypeName + Integer.valueOf(i).toString(), false, inPortType);

                addPort(interceptorComponent, OUTPUT_PORT + portTypeName + Integer.valueOf(i).toString(), true, outPortType);
            }
        }

        fg.generate(hwcPath, interceptorComponentName, ".cpp", cppImplName, interceptorComponentName, INPUT_PORT);

        fg.generate(hwcPath, interceptorComponentName, ".h", headerImplName, interceptorComponentName, tolerance, windowSize);

        flagAsGenerated(interceptorComponent);

        return interceptorComponent;
    }
}
