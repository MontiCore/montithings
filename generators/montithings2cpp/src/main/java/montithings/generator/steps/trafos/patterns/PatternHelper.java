package montithings.generator.steps.trafos.patterns;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings.trafos.BasicTransformations;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PatternHelper extends BasicTransformations {
    protected static final String UNIVARIATE_NAME = "UniAutoregressiveAnomalyDetection";
    protected static final String MULTIVARIATE_NAME = "MultiAutoregressiveAnomalyDetection";

    protected ASTMACompilationUnit getUnivariateComponent(Collection<ASTMACompilationUnit> models, File modelPath) throws Exception {
        ASTMACompilationUnit outermostComponent = this.getOutermostComponent(models);

        for (ASTMACompilationUnit model : models) {
            ASTMCQualifiedName interceptorFullyQName = this.getInterceptorFullyQName(UNIVARIATE_NAME, outermostComponent.getPackage().getQName());
            ASTMCQualifiedName modelFullyQName = TrafoUtil.getFullyQNameFromImports(modelPath, model, model.getComponentType().getName());

            if (interceptorFullyQName.equals(modelFullyQName)) {
                return model;
            }
        }

        return null;
    }

    protected ASTMACompilationUnit getMultivariateComponent(Collection<ASTMACompilationUnit> models, File modelPath) throws Exception {
        ASTMACompilationUnit outermostComponent = this.getOutermostComponent(models);

        for (ASTMACompilationUnit model : models) {
            ASTMCQualifiedName interceptorFullyQName = this.getInterceptorFullyQName(MULTIVARIATE_NAME, outermostComponent.getPackage().getQName());
            ASTMCQualifiedName modelFullyQName = TrafoUtil.getFullyQNameFromImports(modelPath, model, model.getComponentType().getName());

            if (interceptorFullyQName.equals(modelFullyQName)) {
                return model;
            }
        }

        return null;
    }

    protected List<ASTMACompilationUnit> getAllModels(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels) {
        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);
        return allModels;
    }

    protected ASTMACompilationUnit getOutermostComponent(Collection<ASTMACompilationUnit> models) {
        for (ASTMACompilationUnit model : models) {
            if (this.isOutermostComponent(models, model)) {
                return model;
            }
        }

        return null;
    }

    protected boolean isOutermostComponent(Collection<ASTMACompilationUnit> models, ASTMACompilationUnit comp) {
        return TrafoUtil.findParents(models, comp).isEmpty();
    }

    protected ASTMCQualifiedName getInterceptorFullyQName(String interceptorComponentName, String outermostPackage) {
        return MontiThingsMill
                .mCQualifiedNameBuilder()
                .addParts(outermostPackage)
                .addParts(interceptorComponentName)
                .build();
    }
}
