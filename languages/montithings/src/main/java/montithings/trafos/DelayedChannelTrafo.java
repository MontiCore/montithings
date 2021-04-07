// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTPortAccess;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpressionBuilder;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatement;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatementBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTIfStatementBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlockBuilder;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTBehavior;
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

    public void transform(Collection<ASTMACompilationUnit> models, ASTMACompilationUnit targetComp, ASTPortAccess portSource, ASTPortAccess portTarget) throws Exception {
        // A source or target port can either be declared locally or within a subcomponent.
        String sourceTypeName = TrafoUtil.getPortOwningComponentType(targetComp, portSource);

        // Name of the added component, e.g. hierarchy.Example.SourceValueSinkValueDelay
        String channelInterceptorComponentName = "ChannelDelay";

        String channelInterceptorComponentInstanceName =
                (TrafoUtil.replaceDotsWithCamelCase(portSource.getQName()) +
                        TrafoUtil.replaceDotsWithCamelCase(portTarget.getQName()) +
                        "delay").toLowerCase();

        ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(targetComp.getPackage());
        fullyQName.addParts(channelInterceptorComponentName);

        // Adds instantiation statement, e.g. "ChannelDelay sourcevaluesinkvaluedelay";
        addSubComponentInstantiation(targetComp, fullyQName, channelInterceptorComponentInstanceName, createEmptyArguments());

        // Find out the port type. Therefore, first get the component of the source and search for the port.
        // This is only done with the source port as port types have to match anyway
        ASTMCType portType = null;
        try {
            String qName = TrafoUtil.getFullyQNameFromImports(modelPath, targetComp, sourceTypeName).getQName();
            ASTMACompilationUnit compSource = TrafoUtil.getComponentByName(models, qName);
            portType = TrafoUtil.getPortTypeByName(compSource, portSource.getPort());
        } catch (ClassNotFoundException e) {
            //TODO
        } catch (NoSuchElementException e) {
            // model was not found. it is probably a generic type. in this case search for the port within the interfaces
            if (TrafoUtil.isGeneric(targetComp, sourceTypeName)) {
                for (String iface : TrafoUtil.getInterfaces(targetComp, sourceTypeName)) {
                    ASTMACompilationUnit ifaceComp = TrafoUtil.getComponentByName(models, targetComp.getPackage() + "." + iface);
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

        boolean isAlreadyCreated = models.stream()
                .filter(m -> m.getPackage().equals(targetComp.getPackage()))
                .anyMatch(m -> m.getComponentType().getName().equals(channelInterceptorComponentName));

        if (!isAlreadyCreated) {
            // actually creates the model of the intercepting component
            ASTMACompilationUnit channelInterceptorComponent = createCompilationUnit(targetComp.getPackage(), channelInterceptorComponentName);

            addPort(channelInterceptorComponent,
                    "in",
                    false,
                    portType);
            addPort(channelInterceptorComponent,
                    "out",
                    true,
                    portType);

            addBehavior(channelInterceptorComponent);

            flagAsGenerated(channelInterceptorComponent);

            this.additionalTrafoModels.add(channelInterceptorComponent);
            models.add(channelInterceptorComponent);
        }

        // Replaces the old connection
        removeConnection(targetComp, portSource, portTarget);
        addConnection(targetComp, portSource.getQName(), channelInterceptorComponentInstanceName + "." + "in");
        addConnection(targetComp, channelInterceptorComponentInstanceName + "." + "out", portTarget.getQName());
    }

    void addBehavior(ASTMACompilationUnit targetComp) {
        /*
            int index = 0;

            behavior {
                delayNanoseconds(getChannelDelay(index));
                index = index + 1;
            }
        */
        ASTMCJavaBlockBuilder javaBlockBuilder = MontiThingsMill.mCJavaBlockBuilder();

        // Initiate index variable
        addLongFieldDeclaration(targetComp, "index", 0);


        // implement delayNanoseconds(getNsFromMap(index));
        ASTNameExpression indexNameExpression = MontiThingsMill.nameExpressionBuilder().setName("index").build();

        ASTArgumentsBuilder getNsFromMapArgs = MontiThingsMill.argumentsBuilder();
        getNsFromMapArgs.addExpression(indexNameExpression);

        ASTCallExpression getNsFromMapExpression = createCallExpression("getNsFromMap", getNsFromMapArgs.build());

        ASTArgumentsBuilder delayNanosecondsArgs = MontiThingsMill.argumentsBuilder();
        delayNanosecondsArgs.addExpression(getNsFromMapExpression);

        ASTCallExpression delayNanosecondsExpression = createCallExpression("delayNanoseconds", delayNanosecondsArgs.build());

        ASTExpressionStatement delayStatement = MontiThingsMill.expressionStatementBuilder()
                .setExpression(delayNanosecondsExpression)
                .build();
        javaBlockBuilder.addMCBlockStatement(delayStatement);

        // implement index += 1;
        javaBlockBuilder.addMCBlockStatement(createIncrementVariableStatement("index"));

        // implement out = in;
        javaBlockBuilder.addMCBlockStatement(createAssignmentStatement("out", "in"));


        ASTBehavior behavior = addEmptyBehavior(targetComp);
        behavior.setMCJavaBlock(javaBlockBuilder.build());
    }
}
