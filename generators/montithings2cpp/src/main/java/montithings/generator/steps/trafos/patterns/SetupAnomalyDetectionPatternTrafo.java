package montithings.generator.steps.trafos.patterns;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings.generator.codegen.FileGenerator;
import montithings.trafos.MontiThingsTrafo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SetupAnomalyDetectionPatternTrafo extends PatternHelper implements MontiThingsTrafo {
    private static final String TOOL_NAME = "SetupAnomalyDetectionPatternTrafo";
    private static final String UNIVARIATE_IMPL = "hwc/ftl/AutoregressiveAnomalyDetectionImplCpp";
    private static final String UNIVARIATE_HEADER = "hwc/ftl/AutoregressiveAnomalyDetectionImplHeader";
    private static final String MULTIVARIATE_IMPL = "hwc/ftl/MultivariateAutoregressiveAnomalyDetectionImplCpp";
    private static final String MULTIVARIATE_HEADER = "hwc/ftl/MultivariateAutoregressiveAnomalyDetectionImplHeader";
    private static final String INPUT_PORT = "in";
    private static final String OUTPUT_PORT = "out";
    private final int windowSize;
    private final double tolerance;
    private final File modelPath;
    private final File hwcPath;
    private final FileGenerator fg;
    private final boolean hasUnivariateAnomalyDetection;
    private final boolean hasMultivariateAnomalyDetection;

    public SetupAnomalyDetectionPatternTrafo(File modelPath,
                                             int windowSize,
                                             double tolerance,
                                             File hwcPath,
                                             File genSrcDir,
                                             boolean hasUnivariateAnomalyDetection,
                                             boolean hasMultivariateAnomalyDetection) {
        this.windowSize = windowSize;
        this.tolerance = tolerance;
        this.modelPath = modelPath;
        this.hwcPath = hwcPath;
        this.hasUnivariateAnomalyDetection = hasUnivariateAnomalyDetection;
        this.hasMultivariateAnomalyDetection = hasMultivariateAnomalyDetection;
        this.fg = new FileGenerator(genSrcDir, hwcPath);
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

        if (this.hasUnivariateAnomalyDetection && this.getUnivariateComponent(allModels, modelPath) == null) {
            ASTMACompilationUnit univariateComp =
                    this.getInterceptComponent(UNIVARIATE_NAME, targetComp, UNIVARIATE_IMPL, UNIVARIATE_HEADER);
            additionalTrafoModels.add(univariateComp);
        }

        if (this.hasMultivariateAnomalyDetection && this.getMultivariateComponent(allModels, modelPath) == null) {
            ASTMACompilationUnit multivariateComp =
                    this.getInterceptComponent(MULTIVARIATE_NAME, targetComp, MULTIVARIATE_IMPL, MULTIVARIATE_HEADER);
            additionalTrafoModels.add(multivariateComp);
        }

        return additionalTrafoModels;
    }

    private ASTMACompilationUnit getInterceptComponent(String interceptorComponentName, ASTMACompilationUnit outermostComponent,
                                                       String cppImplName, String headerImplName) {
        ASTMCQualifiedName fullyQName = this.getInterceptorFullyQName(interceptorComponentName, outermostComponent.getPackage().getQName());

        addSubComponentInstantiation(outermostComponent, fullyQName, interceptorComponentName.toLowerCase(), createEmptyArguments());

        ASTMACompilationUnit interceptorComponent = createCompilationUnit(outermostComponent.getPackage(), interceptorComponentName);

        for (String portTypeName : NUMERIC_PORTS) {
            ASTMCQualifiedName qualifiedName = MontiThingsMill
                    .mCQualifiedNameBuilder()
                    .addParts("in")
                    .addParts(portTypeName)
                    .build();

            ASTMCType portType = MontiThingsMill
                    .mCQualifiedTypeBuilder()
                    .setMCQualifiedName(qualifiedName)
                    .build();

            addPort(interceptorComponent, INPUT_PORT + portTypeName, false, portType);

            addPort(interceptorComponent, OUTPUT_PORT + portTypeName, true, portType);
        }

        fg.generate(hwcPath, interceptorComponentName, ".cpp", cppImplName, interceptorComponentName, INPUT_PORT);

        fg.generate(hwcPath, interceptorComponentName, ".h", headerImplName, interceptorComponentName, tolerance, windowSize);

        flagAsGenerated(interceptorComponent);

        return interceptorComponent;
    }
}
