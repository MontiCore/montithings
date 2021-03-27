// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTConnector;
import behavior._ast.ASTAfterStatement;
import behavior._ast.ASTAfterStatementBuilder;
import behavior._ast.ASTEveryBlockBuilder;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionBuilder;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpressionBuilder;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatementBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlockBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTMTEveryBlockBuilder;
import montithings._ast.ASTPublishPort;
import montithings._visitor.FindPortNamesVisitor;
import montithings.cocos.MaxOneUpdateInterval;
import montithings.util.TrafoUtil;

import javax.json.JsonObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Trafo which replaces templated ports (external inputs).
 * <p>
 * For each port which should be mocked, a new component is generated and connected to the port.
 * The newly created component then yields recorded messages.
 */

public class ExternalPortMockTrafo extends BasicTransformations implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "ExternalPortMockTrafo";

    private final String mainCompName;
    private final ReplayDataHandler dataHandler;

    private final File modelPath;

    public ExternalPortMockTrafo(File modelPath, File replayDataFile, String mainComp) {
        this.mainCompName = mainComp;
        this.dataHandler = new ReplayDataHandler(replayDataFile);
        this.modelPath = modelPath;
    }

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation: External Input Mock: " + targetComp.getComponentType().getName(), TOOL_NAME);


        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        List<ASTMACompilationUnit> allModels = new ArrayList<>();
        allModels.addAll(originalModels);
        allModels.addAll(addedModels);

        FindPortNamesVisitor visitorPortNames = new FindPortNamesVisitor();
        targetComp.accept(visitorPortNames);

        // get parent components and see if any of the incoming ports is not connected
        // if this is the case the port must be externally connected
        for (String parentName : TrafoUtil.findParents(allModels, targetComp)) {
            ASTMACompilationUnit parentComp = TrafoUtil.getComponentByName(allModels, parentName);
            List<ASTComponentInstantiation> instantiations = TrafoUtil.getInstantiationsByType(parentComp, targetComp.getComponentType().getName());

            for (ASTComponentInstantiation instantiation : instantiations) {
                // for each port check if connection is present
                for (String instanceName : instantiation.getInstancesNames()) {
                    String qNameInstance = parentName + "." + instanceName;
                    // incoming ports
                    for (String portName : visitorPortNames.getIngoingPorts()) {
                        String qNamePort = instanceName + "." + portName;
                        List<ASTConnector> connectorsMatchingTarget = parentComp.getComponentType().getConnectorsMatchingTarget(qNamePort);

                        if (connectorsMatchingTarget.size() == 0) {
                            additionalTrafoModels.add(transform(additionalTrafoModels, parentComp, targetComp, true, qNameInstance, qNamePort, portName));
                        }
                    }

                    // outgoing ports
                    for (String portName : visitorPortNames.getOutgoingPorts()) {
                        String qNamePort = instanceName + "." + portName;
                        List<ASTConnector> connectorsMatchingSource = parentComp.getComponentType().getConnectorsMatchingSource(qNamePort);

                        if (connectorsMatchingSource.size() == 0) {
                            additionalTrafoModels.add(transform(additionalTrafoModels, parentComp, targetComp, false, qNameInstance, qNamePort, portName));
                        }
                    }
                }
            }
        }

        return additionalTrafoModels;
    }

    public ASTMACompilationUnit transform(Collection<ASTMACompilationUnit> additionalTrafoModels,
                                          ASTMACompilationUnit parentComp,
                                          ASTMACompilationUnit targetComp,
                                          boolean isIngoingPort,
                                          String qNameInstance,
                                          String qNamePort,
                                          String port) throws Exception {
        // naming convention as follows <Component><Port>Mock, e.g. SourceSensorMock
        String mockedComponentName = TrafoUtil.capitalize(targetComp.getComponentType().getName()) + TrafoUtil.capitalize(port) + "Mock";

        ASTMACompilationUnit mockedPortComp;

        boolean isAlreadyCreated = additionalTrafoModels.stream().anyMatch(m -> m.getComponentType().getName().equals(mockedComponentName));
        if (isAlreadyCreated) {
            mockedPortComp = TrafoUtil.getComponentByName(additionalTrafoModels, targetComp.getPackage() + "." + mockedComponentName);
        } else {
            // adds new subcomponent representing the external input
            mockedPortComp = createCompilationUnit(targetComp.getPackage(), mockedComponentName);

            if (isIngoingPort) {
                addBehavior(mockedPortComp, qNameInstance, port);
            }

            // the corresponding connected mocking port has the reversed direction
            addPort(mockedPortComp,
                    isIngoingPort ? "out" : "in",
                    isIngoingPort,
                    TrafoUtil.getPortTypeByName(targetComp, port));
        }

        // Instantiate the mocked port in the parent component
        ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(targetComp.getPackage());
        fullyQName.addParts(mockedComponentName);
        addSubComponentInstantiation(parentComp, fullyQName, mockedComponentName.toLowerCase(), createEmptyArguments());

        if (isIngoingPort) {
            addConnection(parentComp, mockedComponentName.toLowerCase() + ".out", qNamePort);
        } else {
            addConnection(parentComp, qNamePort, mockedComponentName.toLowerCase() + ".in");
        }

        return mockedPortComp;
    }

    void addBehavior(ASTMACompilationUnit comp, String qNameComp, String portName) {
        List<JsonObject> recordings = dataHandler.getRecordings(qNameComp, portName);

        // create "every" block
        ASTEveryBlockBuilder everyBlock = MontiThingsMill.everyBlockBuilder();
        everyBlock.setSIUnitLiteral(TrafoUtil.createSIUnitLiteral(100, "h"));

        ASTMCJavaBlockBuilder javaBlock = MontiThingsMill.mCJavaBlockBuilder();

        // fill the "every" block with after statements
        for (JsonObject recording : recordings) {
            long timestamp = recording.getJsonNumber("timestamp").longValue();

            // returns serialized value e.g. { "value0": 726}
            String valueSerialized = recording.getString("msg_content");

            String value = TrafoUtil.parseJson(valueSerialized).get("value0").toString();
            javaBlock.addMCBlockStatement(addAfterBehaviorBlock(timestamp, value));
        }

        everyBlock.setMCJavaBlock(javaBlock.build());
        ASTMTEveryBlockBuilder mtEveryBlock = MontiThingsMill.mTEveryBlockBuilder();
        mtEveryBlock.setEveryBlock(everyBlock.build());
        comp.getComponentType().getBody().addArcElement(mtEveryBlock.build());
    }

    private ASTAfterStatement addAfterBehaviorBlock(long timestamp, String value) {
        ASTAfterStatementBuilder afterStatement = MontiThingsMill.afterStatementBuilder();
        afterStatement.setSIUnitLiteral(TrafoUtil.createSIUnitLiteral(timestamp, "ns"));

        ASTMCJavaBlockBuilder javaBlock = MontiThingsMill.mCJavaBlockBuilder();

        ASTExpressionStatementBuilder astExpressionStatement = MontiThingsMill.expressionStatementBuilder();
        ASTAssignmentExpressionBuilder assignmentExpression = MontiThingsMill.assignmentExpressionBuilder();

        // implementing " <port> = <value>
        // left side
        ASTNameExpression nameExpression = MontiThingsMill.nameExpressionBuilder().setName("out").build();
        assignmentExpression.setLeft(nameExpression);

        assignmentExpression.setOperator(ASTConstantsAssignmentExpressions.EQUALS);

        // right side
        // TODO depends on the port type
        ASTLiteralExpressionBuilder literalExpression = MontiThingsMill.literalExpressionBuilder();

        ASTNatLiteralBuilder natLiteral = MontiThingsMill.natLiteralBuilder();
        natLiteral.setDigits(value);

        literalExpression.setLiteral(natLiteral.build());
        assignmentExpression.setRight(literalExpression.build());

        astExpressionStatement.setExpression(assignmentExpression.build());
        javaBlock.addMCBlockStatement(astExpressionStatement.build());

        javaBlock.addMCBlockStatement(createLogStatement("Sending input=" + value + " on <out> port"));

        // implement publish out;
        ASTPublishPort publishPort = MontiThingsMill.publishPortBuilder()
                .addPublishedPorts("out")
                .build();
        javaBlock.addMCBlockStatement(publishPort);

        afterStatement.setMCJavaBlock(javaBlock.build());

        return afterStatement.build();
    }
}
