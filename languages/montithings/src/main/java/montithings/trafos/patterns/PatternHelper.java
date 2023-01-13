package montithings.trafos.patterns;

import arcbasis._ast.ASTPortAccess;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindConnectionsVisitor;
import montithings.trafos.BasicTransformations;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.*;

public class PatternHelper extends BasicTransformations {
    /**
     * Returns all connections inside this component i.e. in basic-input-output only the example component
     * has subcomponents which are connected and thus one connection would be returned
     */
    protected List<FindConnectionsVisitor.Connection> getConnections(ASTMACompilationUnit comp) {
        FindConnectionsVisitor visitor = new FindConnectionsVisitor();
        comp.accept(visitor.createTraverser());
        return visitor.getConnections();
    }

    protected boolean isNumericPort(ASTMCType portType) {
        List<String> numericPortTypes = Arrays.asList("int", "double", "float");
        return numericPortTypes.contains(portType.toString());
    }

    protected List<ASTMACompilationUnit> getAllModels(Collection<ASTMACompilationUnit> originalModels,
                                                    Collection<ASTMACompilationUnit> addedModels) {
        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);
        return allModels;
    }

    protected ASTMCType getPortType(ASTPortAccess port, ASTMACompilationUnit comp, List<ASTMACompilationUnit> models, File modelPath) throws Exception {
        String sourceTypeName = TrafoUtil.getPortOwningComponentType(comp, port);

        ASTMCType portType = null;

        try {
            String qName = TrafoUtil.getFullyQNameFromImports(modelPath, comp, sourceTypeName).getQName();
            ASTMACompilationUnit compSource = TrafoUtil.getComponentByName(models, qName);
            portType = TrafoUtil.getPortTypeByName(compSource, port.getPort());
        }

        catch (ClassNotFoundException e) {
            // portType will be null which is caught later on
        }

        catch (NoSuchElementException e) {
            // model was not found. it is probably a generic type. in this case search for the port within the interfaces
            if (TrafoUtil.isGeneric(comp, sourceTypeName)) {
                for (String iface : TrafoUtil.getInterfaces(comp, sourceTypeName)) {
                    ASTMACompilationUnit ifaceComp = TrafoUtil
                            .getComponentByName(models, comp.getPackage() + "." + iface);
                    try {
                        portType = TrafoUtil.getPortTypeByName(ifaceComp, port.getPort());
                    }
                    catch (Exception e1) {
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

    protected ASTMACompilationUnit getInterceptComponent(ASTPortAccess source, ASTPortAccess target, ASTMACompilationUnit comp,
                                                       List<ASTMACompilationUnit> models, ASTMCType portType) {

        String interceptorComponentName = this.getInterceptorComponentName(source, target, comp, models);

        ASTMCQualifiedName fullyQName = this.getInterceptorFullyQName(interceptorComponentName, comp);

        addSubComponentInstantiation(comp, fullyQName, interceptorComponentName.toLowerCase(), createEmptyArguments());

        ASTMACompilationUnit interceptorComponent = createCompilationUnit(comp.getPackage(), interceptorComponentName);


        // TODO: add new AutoregressiveAnomalyDetection(this.windowSize, this.tolerance); as behaviour
        //  via addBehavior(channelInterceptorComponent, delays);

        addPort(interceptorComponent, "in", false, portType);
        addPort(interceptorComponent, "out", true, portType);

        flagAsGenerated(interceptorComponent);

        // Replace the old connection
        removeConnection(comp, source, target);
        addConnection(comp, source.getQName(), interceptorComponentName.toLowerCase() + "." + "in");
        addConnection(comp, interceptorComponentName.toLowerCase() + "." + "out", target.getQName());

        return interceptorComponent;
    }

    protected String getInterceptorComponentName(ASTPortAccess source, ASTPortAccess target, ASTMACompilationUnit comp, List<ASTMACompilationUnit> models) {
        String newNameEnding = "AutoregressiveAnomalyDetection";
        String newName = newNameEnding;

        List<String> qCompSourceNames = new ArrayList<>();
        List<String> qCompTargetNames = new ArrayList<>();

        if (source.isPresentComponent()) {
            qCompSourceNames = TrafoUtil
                    .getFullyQInstanceName(models, comp, source.getComponent());
        }

        if (target.isPresentComponent()) {
            qCompTargetNames = TrafoUtil
                    .getFullyQInstanceName(models, comp, target.getComponent());
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

    protected ASTMCQualifiedName getInterceptorFullyQName(String interceptorComponentName, ASTMACompilationUnit comp) {
        ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(comp.getPackage());
        fullyQName.addParts(interceptorComponentName);

        return fullyQName;
    }
}
