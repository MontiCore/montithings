// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentInstantiation;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionBuilder;
import de.monticore.expressions.assignmentexpressions._ast.ASTConstantsAssignmentExpressions;
import de.monticore.expressions.commonexpressions._ast.*;
import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import de.monticore.statements.mccommonstatements._ast.*;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.mcbasictypes._ast.*;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTIsPresentExpression;
import montithings._visitor.FindPortNamesVisitor;
import montithings.util.TrafoUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Transformer for delaying computation times according to the recorded data.
 * <p>
 * This is done as follows:
 * The original component is moved into a new wrapping component where an additional DELAY subcomponent is interposed
 * after the ports. When a message is received, a timestamp is calculated by the DELAY component.
 * The DELAY component is aware of how long the computation should take
 * and adds delay in case that the computation is completed too fast.
 * <p>
 * This transformation moves the original component into a wrapping one, creates the new subcomponent and
 * rewires all connections.
 */


public class DelayedComputationTrafo extends BasicTransformations implements MontiThingsTrafo {
    protected static final String TOOL_NAME = "ExternalPortMock";

    private final ReplayDataHandler dataHandler;

    private final File modelPath;

    public DelayedComputationTrafo(File modelPath, File replayDataFile) {
        this.dataHandler = new ReplayDataHandler(replayDataFile);
        this.modelPath = modelPath;
    }

    public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                      Collection<ASTMACompilationUnit> addedModels,
                                                      ASTMACompilationUnit targetComp) throws Exception {
        Log.info("Apply transformation: Delayed Computation: " + targetComp.getComponentType().getName(), TOOL_NAME);

        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        // retrieves a list of all ports of the original component
        FindPortNamesVisitor visitor = new FindPortNamesVisitor();
        targetComp.accept(visitor);
        Set<String> origIngoingPorts = visitor.getIngoingPorts();
        Set<String> origOutgoingPorts = visitor.getOutgoingPorts();


        // delay is dependent of the actual instance, hence, for each instance, we need a new component type
        for (String parentName : TrafoUtil.findParents(originalModels, targetComp)) {
            ASTMACompilationUnit parentComp = TrafoUtil.getComponentByName(originalModels, parentName);
            List<ASTComponentInstantiation> instantiations = TrafoUtil.getInstantiationsByType(parentComp, targetComp.getComponentType().getName());

            for (ASTComponentInstantiation instantiation : instantiations) {
                for (String instanceName : instantiation.getInstancesNames()) {
                    String qNameInstance = parentName + "." + instanceName;
                    additionalTrafoModels.addAll(transform(targetComp, qNameInstance, origIngoingPorts, origOutgoingPorts));

                    // wherever the original component was initiated, the declaration has to be changed.
                    // E.g. "Sink sink" becomes "SinkWrapper sink"
                    replaceComponentInstantiationType(parentComp, modelPath,
                            targetComp.getComponentType().getName(),
                            TrafoUtil.replaceDotsWithCamelCase(qNameInstance) + "Wrapper");
                }
            }
        }

        return additionalTrafoModels;
    }


    private Collection<ASTMACompilationUnit> transform(
            ASTMACompilationUnit origComp, String origQNameInstance,
            Set<String> origIngoingPorts, Set<String> origOutgoingPorts) throws Exception {

        Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

        /* ============================================================ */
        /* ======================= COMPONENT CREATION ================= */
        /* ============================================================ */

        // creates a wrapping component, e.g. SinkWrapper for component instance sink
        String compName = TrafoUtil.replaceDotsWithCamelCase(origQNameInstance);
        String compWrapperName = compName + "Wrapper";
        ASTMACompilationUnit compWrapper = createCompilationUnit(origComp.getPackage(), compWrapperName);

        // copies parameters from original model
        List<ASTArcParameter> parameterList = origComp.getComponentType().getHead().getArcParameterList();
        compWrapper.getComponentType().getHead().setArcParameterList(parameterList);

        // For each wrapping component, a new DELAY component is required
        ASTMACompilationUnit delayComp;
        String delayCompName = compWrapperName + "ComputationDelay";

        delayComp = createCompilationUnit(origComp.getPackage(), delayCompName);

        /* ============================================================ */
        /* ======================= INSTANTIATIONS ===================== */
        /* ============================================================ */
        // In case of parameterized components the wrapping component takes the same parameters as the original one and simply forwards it
        // e.g. component LowPassFilterWrapper (int threshold, int defaultValue) {
        //          LowPassFilter lpf (threshold, defaultValue);
        //      }
        List<String> parameterStringList = parameterList.stream()
                .map(ASTArcParameter::getName)
                .collect(Collectors.toList());
        ASTArguments arguments = createArguments(parameterStringList);

        String origCompName = origComp.getComponentType().getName();
        ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(origComp.getPackage());
        fullyQName.addParts(origCompName);
        addSubComponentInstantiation(compWrapper, fullyQName, origCompName.toLowerCase(), arguments);

        // Instantiation of the delay component within the wrapping one
        ASTMCQualifiedName fullyQNameDelay = TrafoUtil.copyASTMCQualifiedName(origComp.getPackage());
        fullyQNameDelay.addParts(delayCompName);
        addSubComponentInstantiation(compWrapper, fullyQNameDelay, delayCompName.toLowerCase(), createEmptyArguments());

        /* ============================================================ */
        /* ======================= PORT CREATION ====================== */
        /* ============================================================ */
        // the wrapper component requires the same ports
        // hence, mirror the port declarations
        for (String port : origIngoingPorts) {
            addPort(compWrapper, port, false,
                    TrafoUtil.getPortTypeByName(origComp, port));
        }

        for (String port : origOutgoingPorts) {
            addPort(compWrapper, port, true,
                    TrafoUtil.getPortTypeByName(origComp, port));
        }

        // add ports for the delay component
        // creates forwarding ports for each port of the original component
        for (String origIngoingPort : origIngoingPorts) {
            addPort(delayComp,
                    origIngoingPort + "_before_in",
                    false,
                    TrafoUtil.getPortTypeByName(origComp, origIngoingPort));
        }

        for (String origOutgoingPort : origOutgoingPorts) {
            addPort(delayComp,
                    origOutgoingPort + "_out",
                    true,
                    TrafoUtil.getPortTypeByName(origComp, origOutgoingPort));
            addPort(delayComp,
                    origOutgoingPort + "_after_in",
                    false,
                    TrafoUtil.getPortTypeByName(origComp, origOutgoingPort));
        }

        /* ============================================================ */
        /* ======================= CONNECTION CREATION ================ */
        /* ============================================================ */

        // for each incoming port...
        origIngoingPorts.forEach(port -> {
            // ... forward it to the original component
            addConnection(compWrapper,
                    port,
                    origCompName.toLowerCase() + "." + port);
            // ... but also to the delay component
            addConnection(compWrapper,
                    port,
                    delayCompName.toLowerCase() + "." + port + "_before_in");
        });

        // for each outgoing port...
        origOutgoingPorts.forEach(port -> {
            // ... forward it to the delay component
            addConnection(compWrapper,
                    origCompName.toLowerCase() + "." + port,
                    delayCompName.toLowerCase() + "." + port + "_after_in");
            // ... and forward the result from the delay component to the outermost port of the wrapping component
            addConnection(compWrapper,
                    delayCompName.toLowerCase() + "." + port + "_out",
                    port);
        });


        List<String> origPortsIn = new ArrayList<>(origIngoingPorts);
        List<String> origPortsOut = new ArrayList<>(origOutgoingPorts);
        addBehaviorDelayComp(delayComp, origQNameInstance, origPortsIn, origPortsOut);

        flagAsGenerated(compWrapper);
        flagAsGenerated(delayComp);
        flagAsWrapped(origComp);

        additionalTrafoModels.add(compWrapper);
        additionalTrafoModels.add(delayComp);

        return additionalTrafoModels;
    }

    private void addBehaviorDelayComp(ASTMACompilationUnit comp, String origQNameInstance, List<String> origPortsIn, List<String> origPortsOut) {
        /*  Behavior looks like the following
        port in int in1_before_in;
        port in int in2_before_in;
        port in int in3_before_in;

        port in int out1_after_in;
        port in int out2_after_in;

        port out int out1_out;
        port out int out2_out;

        // 1. Variable initiations
        int index_msg = 0;
        int index_msg_from_comp = 0;

        inf targetDelay = 0;


        behavior {
            // Whenever an input from the wrapping component is present, save its arrival timestamp
            // 2.1 Condition
            if (in1_before_in? || (in2_before_in? || in3_before_in?)) {
            // 2.2 then statement
            // 2.3 storeNsInMap
             storeNsInMap(index_msg, getNanoTimestamp());

             // 2.4 increase index_msg
             index_msg+=1;
            }

            // in case the original components yields an output, lookup its initial arrival time and add a delay if necessary
            // 3.1 Condition
            if (out1_after_in? || out2_after_in?) {
             if (index_msg_from_comp == 0) {
                targetDelay = 999;
             }
             if (index_msg_from_comp == 1) {
                targetDelay = 1001;
             }
             ...

             // 3.2 implement delayNanoseconds()
             delayNanoseconds(subtract(targetDelay, subtract(getNanoTimestamp(), getNsFromMap(index_msg_from_comp)))))
             // 3.3 index_msg_from_comp+=1;
             index_msg_from_comp+=1;
             // 3.4 implement assignments()
             out1_out = out2_after_in?;
             out2_out = out2_after_in?;
            }
        }
        */

        // 1. Variable initiations
        addLongFieldDeclaration(comp, "index_msg", 0);
        addLongFieldDeclaration(comp, "index_msg_from_comp", 0);
        addLongFieldDeclaration(comp, "targetDelay", 0);

        ASTBehavior behavior = addEmptyBehavior(comp);

        // 2.1 Condition
        List<String> portsBeforeIn = origPortsIn.stream().map(p -> p.concat("_before_in")).collect(Collectors.toList());
        if (!portsBeforeIn.isEmpty()) {
            ASTExpression conditionIncomingPorts = createCondition(portsBeforeIn);

            // 2.3 storeNsInMap: arguments
            ASTArgumentsBuilder storeNsInMapArgs = MontiThingsMill.argumentsBuilder();

            // First argument
            ASTNameExpression indexMsgNameExpression = MontiThingsMill.nameExpressionBuilder().setName("index_msg").build();
            storeNsInMapArgs.addExpression(indexMsgNameExpression);

            // Second argument
            ASTArguments emptyArgs = MontiThingsMill.argumentsBuilder().build();
            ASTCallExpression getTsCallExpression = createCallExpression("getNanoTimestamp", emptyArgs);
            storeNsInMapArgs.addExpression(getTsCallExpression);

            // create storeNsInMap statement
            ASTCallExpression storeNsInMapCallExpression = createCallExpression("storeNsInMap", storeNsInMapArgs.build());
            ASTExpressionStatement storeNsInMapCallExpressionStatement = MontiThingsMill.expressionStatementBuilder()
                    .setExpression(storeNsInMapCallExpression)
                    .build();

            // add statement to then block
            // 2.2 then statement
            ASTMCJavaBlockBuilder ingoingThenBlock = MontiThingsMill.mCJavaBlockBuilder();
            //ingoingThenBlock.addMCBlockStatement(createLogStatement("received message from wrapping component: $index_msg"));
            ingoingThenBlock.addMCBlockStatement(storeNsInMapCallExpressionStatement);

            // 2.4 increase index_msg
            ASTMCBlockStatement incIndexStatement = createIncrementVariableStatement("index_msg");
            ingoingThenBlock.addMCBlockStatement(incIndexStatement);

            ASTIfStatementBuilder ifStatementIncomingBuilder = MontiThingsMill.ifStatementBuilder();
            ifStatementIncomingBuilder.setCondition(conditionIncomingPorts);
            ifStatementIncomingBuilder.setThenStatement(ingoingThenBlock.build());
            ifStatementIncomingBuilder.setElseStatementAbsent();

            behavior.getMCJavaBlock().addMCBlockStatement(ifStatementIncomingBuilder.build());
        }

        // 3.1 Condition
        List<String> portsAfterIn = origPortsOut.stream().map(p -> p.concat("_after_in")).collect(Collectors.toList());
        if (portsAfterIn.isEmpty()) {
            // There is no outgoing port
            return;
        }
        ASTExpression conditionOutgoingPorts = createCondition(portsAfterIn);

        ASTMCJavaBlockBuilder outgoingThenBlock = MontiThingsMill.mCJavaBlockBuilder();
        //outgoingThenBlock.addMCBlockStatement(createLogStatement("received message from original component; index: $index_msg_from_comp"));

        // set targetLatencies
        HashMap<Integer, Long> computationLatencies = dataHandler.getComputationLatencies(origQNameInstance);

        for (Integer index : computationLatencies.keySet()) {
            outgoingThenBlock.addMCBlockStatement(addSetTargetDelayIfStatement(index, computationLatencies.get(index)));
        }

        outgoingThenBlock.addMCBlockStatement(createDelayStatement());

        // 3.3 index_msg_from_comp+=1;
        ASTMCBlockStatement incIndexFromCompStatement = createIncrementVariableStatement("index_msg_from_comp");
        outgoingThenBlock.addMCBlockStatement(incIndexFromCompStatement);

        // 3.4 implement assignments()
        for (String port : origPortsOut) {
            //<port>_out = <port>_after_in?;
            outgoingThenBlock.addMCBlockStatement(createAssignmentStatement(port + "_out", port + "_after_in"));
        }

        ASTIfStatementBuilder ifStatementOutgoingBuilder = MontiThingsMill.ifStatementBuilder();
        ifStatementOutgoingBuilder.setCondition(conditionOutgoingPorts);
        ifStatementOutgoingBuilder.setThenStatement(outgoingThenBlock.build());
        ifStatementOutgoingBuilder.setElseStatementAbsent();

        behavior.getMCJavaBlock().addMCBlockStatement(ifStatementOutgoingBuilder.build());
    }

    private ASTExpressionStatement createDelayStatement() {
        // implements delayNanoseconds(subtract(targetDelay, subtract(getNanoTimestamp(), getNsFromMap(index_msg_from_comp)))))

        /* INNER SUBTRACT METHOD */
        // implement first argument getNanoTimestamp()
        ASTArguments emptyArgs = MontiThingsMill.argumentsBuilder().build();
        ASTCallExpression getTsCallExpression = createCallExpression("getNanoTimestamp", emptyArgs);

        // implement getNsFromMap(index_msg_from_comp)
        ASTNameExpression indexMsgFromCompNameExpression = MontiThingsMill.nameExpressionBuilder().setName("index_msg_from_comp").build();
        ASTArgumentsBuilder getNsFromMapArgs = MontiThingsMill.argumentsBuilder();
        getNsFromMapArgs.addExpression(indexMsgFromCompNameExpression);

        ASTCallExpression getNsFromMapCallExpression = createCallExpression("getNsFromMap", getNsFromMapArgs.build());

        // create argument list of inner subtract method
        ASTArgumentsBuilder innerSubtractArgs = MontiThingsMill.argumentsBuilder();
        innerSubtractArgs.addExpression(getTsCallExpression);
        innerSubtractArgs.addExpression(getNsFromMapCallExpression);

        // build inner subtract method
        ASTCallExpression innerSubtractCallExpression = createCallExpression("subtract", innerSubtractArgs.build());

        /* OUTER SUBTRACT METHOD */
        // create the argument list
        ASTArgumentsBuilder outerSubtractArgs = MontiThingsMill.argumentsBuilder();

        // first argument is simply a name expression
        ASTNameExpression targetDelayNameExpression = MontiThingsMill.nameExpressionBuilder().setName("targetDelay").build();
        outerSubtractArgs.addExpression(targetDelayNameExpression);

        // second argument is the inner subtract call expression
        outerSubtractArgs.addExpression(innerSubtractCallExpression);

        // build outer subtract method
        ASTCallExpression outerSubtractCallExpression = createCallExpression("subtract", outerSubtractArgs.build());

        // 3.2 finally implement delayNanoseconds(...)
        ASTArgumentsBuilder delayNanosecondsArgs = MontiThingsMill.argumentsBuilder();
        delayNanosecondsArgs.addExpression(outerSubtractCallExpression);
        ASTCallExpression delayNanosecondsExpression = createCallExpression("delayNanoseconds", delayNanosecondsArgs.build());

        return MontiThingsMill.expressionStatementBuilder()
                .setExpression(delayNanosecondsExpression)
                .build();
    }

    private ASTExpression createCondition(List<String> ports) {
        // for each port, create a ASTIsPresentExpression
        List<ASTIsPresentExpression> isPresentExpressions = new ArrayList<>();

        for (String port : ports) {
            ASTNameExpression nameExpression = MontiThingsMill.nameExpressionBuilder().setName(port).build();

            ASTIsPresentExpression isPresentExpression = MontiThingsMill.isPresentExpressionBuilder()
                    .setNameExpression(nameExpression)
                    .build();

            isPresentExpressions.add(isPresentExpression);
        }

        // if only one port is present, we dont need to concatenate multiple expressions with a || operation
        if (isPresentExpressions.size() == 1) {
            return isPresentExpressions.get(0);
        }

        // otherwise start with an ASTBooleanOrOpExpression and add all expressions
        ASTBooleanOrOpExpressionBuilder condition = MontiThingsMill.booleanOrOpExpressionBuilder();

        condition.setRight(isPresentExpressions.get(0));
        condition.setOperator("||");

        // delete added expression and recursively add add the others
        ports.remove(0);
        condition.setLeft(createCondition(ports));

        return condition.build();
    }

    private ASTMCBlockStatement addSetTargetDelayIfStatement(int index, long delay) {
        /* implements
             if (index_msg_from_comp == <index>) {
                targetDelay = <delay>;
             }
         */
        ASTNameExpression indexNameExpression = MontiThingsMill.nameExpressionBuilder().setName("index_msg_from_comp").build();

        // Building condition
        ASTNatLiteralBuilder rightNatLiteralBuilder = MontiThingsMill.natLiteralBuilder();
        rightNatLiteralBuilder.setDigits(String.valueOf(index));

        ASTLiteralExpressionBuilder rightExpressionBuilder = MontiThingsMill.literalExpressionBuilder();
        rightExpressionBuilder.setLiteral(rightNatLiteralBuilder.build());

        ASTEqualsExpressionBuilder conditionBuilder = MontiThingsMill.equalsExpressionBuilder();
        conditionBuilder.setLeft(indexNameExpression);
        conditionBuilder.setOperator("==");
        conditionBuilder.setRight(rightExpressionBuilder.build());

        // Building then statement
        ASTNameExpression delayNameExpression = MontiThingsMill.nameExpressionBuilder().setName("targetDelay").build();

        ASTNatLiteral delayNatLiteral = MontiThingsMill.natLiteralBuilder().setDigits(String.valueOf(delay)).build();
        ASTLiteralExpression delayLiteralExpression = MontiThingsMill.literalExpressionBuilder().setLiteral(delayNatLiteral).build();

        ASTAssignmentExpressionBuilder assignmentExpressionBuilder = MontiThingsMill.assignmentExpressionBuilder();
        assignmentExpressionBuilder.setLeft(delayNameExpression);
        assignmentExpressionBuilder.setOperator(ASTConstantsAssignmentExpressions.EQUALS);
        assignmentExpressionBuilder.setRight(delayLiteralExpression);

        ASTExpressionStatementBuilder thenExpressionStatementBuilder = MontiThingsMill.expressionStatementBuilder();
        thenExpressionStatementBuilder.setExpression(assignmentExpressionBuilder.build());

        ASTMCJavaBlockBuilder thenStatementBuilder = MontiThingsMill.mCJavaBlockBuilder();
        thenStatementBuilder.addMCBlockStatement(thenExpressionStatementBuilder.build());

        ASTIfStatementBuilder ifStatementBuilder = MontiThingsMill.ifStatementBuilder();
        ifStatementBuilder.setCondition(conditionBuilder.build());
        ifStatementBuilder.setThenStatement(thenStatementBuilder.build());
        ifStatementBuilder.setElseStatementAbsent();

        return ifStatementBuilder.build();
    }
}

