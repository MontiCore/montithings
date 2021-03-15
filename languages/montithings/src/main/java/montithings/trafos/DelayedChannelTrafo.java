// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTPortAccess;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.FindConnectionsVisitor;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Transformer for delaying channels.
 * <p>
 * Connections which should be delayed are intercepted by a new component which just forwards the messages,
 * but adds a certain delay.
 */

public class DelayedChannelTrafo extends BasicTransformations implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "DelayedChannelTrafo";

    private Collection<ASTMACompilationUnit> additionalTrafoModels;

    private final ReplayDataHandler dataHandler;

    private final File modelPath;

    public DelayedChannelTrafo(File modelPath, File replayDataFile) {
        this.dataHandler = new ReplayDataHandler(replayDataFile);
        this.modelPath = modelPath;
    }

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation: Delayed Channels: " + targetComp.getComponentType().getName(), TOOL_NAME);

        this.additionalTrafoModels = new ArrayList<>();
        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);

        FindConnectionsVisitor visitor = new FindConnectionsVisitor();
        targetComp.accept(visitor);
        List<FindConnectionsVisitor.Connection> connections = visitor.getConnections();

        for (FindConnectionsVisitor.Connection connection : connections) {
            transform(allModels, targetComp, connection.source, connection.target);
        }

        return this.additionalTrafoModels;
    }

    public void transform(Collection<ASTMACompilationUnit> models, ASTMACompilationUnit comp, ASTPortAccess portSource, ASTPortAccess portTarget) throws Exception {
        // A source or target port can either be declared locally or within a subcomponent.
        String sourceTypeName = TrafoUtil.getPortOwningComponentType(comp, portSource);

        // Name of the added component, e.g. hierarchy.Example.SourceValueSinkValueDelay
        String channelInterceptorComponentName =
                TrafoUtil.replaceDotsWithCamelCase(portSource.getQName()) +
                        TrafoUtil.replaceDotsWithCamelCase(portTarget.getQName()) +
                        "Delay";

        channelInterceptorComponentName = TrafoUtil.capitalize(channelInterceptorComponentName);

        ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(comp.getPackage());
        fullyQName.addParts(channelInterceptorComponentName);

        // Adds instantiation statement, e.g. "SourceValueSinkValueDelay sourcevaluesinkvaluedelay";
        addSubComponentInstantiation(comp, fullyQName, channelInterceptorComponentName.toLowerCase(), createEmptyArguments());

        // Find out the port type. Therefore, first get the component of the source and search for the port.
        // This is only done with the source port as port types have to match anyway
        // TODO remove null if possible
        ASTMCType portType = null;
        try {
            String qName = TrafoUtil.getFullyQNameFromImports(modelPath, comp, sourceTypeName).getQName();
            ASTMACompilationUnit compSource = TrafoUtil.getComponentByName(models, qName);
            portType = TrafoUtil.getPortTypeByName(compSource, portSource.getPort());
        } catch (ClassNotFoundException e) {
            String test = "asd";
            //TODO
        } catch (NoSuchElementException e) {
            // model was not found. it is probably a generic type. in this case search for the port within the interfaces
            if (TrafoUtil.isGeneric(comp, sourceTypeName)) {
                for (String iface : TrafoUtil.getInterfaces(comp, sourceTypeName)) {
                    ASTMACompilationUnit ifaceComp = TrafoUtil.getComponentByName(models, comp.getPackage() + "." + iface);
                    try {
                        portType = TrafoUtil.getPortTypeByName(ifaceComp, portSource.getPort());
                    } catch (Exception e1) {
                        //ignore, check next iface
                    }
                }
            }
        }
        if (portType == null) {
            throw new NoSuchElementException("No such port instance found which is named " + portSource.getPort());
        }


        // actually creates the model of the intercepting component
        ASTMACompilationUnit channelInterceptorComponent = createCompilationUnit(comp.getPackage(), channelInterceptorComponentName);


        // TODO
        addEmptyBehavior(channelInterceptorComponent);

        addPort(channelInterceptorComponent,
                "in",
                false,
                portType);
        addPort(channelInterceptorComponent,
                "out",
                true,
                portType);

        this.additionalTrafoModels.add(channelInterceptorComponent);

        // Replaces the old connection
        removeConnection(comp, portSource, portTarget);
        addConnection(comp, portSource.getQName(), channelInterceptorComponentName.toLowerCase() + "." + "in");
        addConnection(comp, channelInterceptorComponentName.toLowerCase() + "." + "out", portTarget.getQName());
    }
}
