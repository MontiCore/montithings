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
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTExpressionStatementBuilder;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlockBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTMTEveryBlockBuilder;
import montithings._visitor.FindPortNamesVisitor;
import montithings.util.TrafoUtil;

import javax.json.JsonObject;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Trafo which replaces templated ports (external inputs).
 * <p>
 * For each port which should be mocked, a new component is generated and
 * connected to the port.
 * The newly created component then yields recorded messages.
 */

public class ExternalPortMockTrafo extends BasicTransformations implements MontiThingsTrafo {
  protected static final String TOOL_NAME = "ExternalPortMockTrafo";

  private final ReplayDataHandler dataHandler;

  public ExternalPortMockTrafo(File modelPath, File replayDataFile, String mainComp) {
    this.dataHandler = new ReplayDataHandler(replayDataFile);
  }

  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
    Collection<ASTMACompilationUnit> addedModels,
    ASTMACompilationUnit targetComp) throws Exception {
    Log
      .info("Apply transformation: External Input Mock: " + targetComp.getComponentType().getName(),
        TOOL_NAME);

    Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

    List<ASTMACompilationUnit> allModels = new ArrayList<>();
    allModels.addAll(originalModels);
    allModels.addAll(addedModels);

    FindPortNamesVisitor visitorPortNames = new FindPortNamesVisitor();
    targetComp.accept(visitorPortNames.createTraverser());

    // get parent components and see if any of the incoming ports is not connected
    // if this is the case the port must be externally connected
    for (String parentName : TrafoUtil.findParents(allModels, targetComp)) {
      ASTMACompilationUnit parentComp = TrafoUtil.getComponentByName(allModels, parentName);
      List<ASTComponentInstantiation> instantiations = TrafoUtil
        .getInstantiationsByType(parentComp, targetComp.getComponentType().getName());

      for (ASTComponentInstantiation instantiation : instantiations) {
        // for each port check if connection is present
        for (String instanceName : instantiation.getInstancesNames()) {
          for (String qNameInstance : TrafoUtil
            .getFullyQInstanceName(allModels, parentComp, instanceName)) {
            // workaround: sometimes the parent component was wrapped during a computation delay transformation
            // if this is the case the instance name has to be adjusted.
            // e.g. smartHome.thermostat.thermostat.ui should be smartHome.thermostat.ui
            if (wasWrapped(parentComp)) {
              List<String> tokens = Collections.list(new StringTokenizer(qNameInstance, "."))
                .stream()
                .map(token -> (String) token)
                .collect(Collectors.toList());
              tokens.remove(tokens.size() - 2);

              qNameInstance = String.join(".", tokens);
            }

            // incoming ports
            for (String portName : visitorPortNames.getIngoingPorts()) {
              String qNamePort = instanceName + "." + portName;
              List<ASTConnector> connectorsMatchingTargetAtParent = parentComp.getComponentType()
                .getConnectorsMatchingTarget(qNamePort);

              // if no target defined in the parent, however, there could be still a connection within the own model
              List<ASTConnector> connectorsMatchingTargetAtOwnComp = targetComp.getComponentType()
                .getConnectorsMatchingTarget(qNamePort);

              if (connectorsMatchingTargetAtParent.size() == 0
                && connectorsMatchingTargetAtOwnComp.size() == 0) {
                additionalTrafoModels.add(
                  transform(additionalTrafoModels, parentComp, targetComp, true, qNameInstance,
                    qNamePort, portName));
              }
            }

            // outgoing ports
            for (String portName : visitorPortNames.getOutgoingPorts()) {
              String qNamePort = instanceName + "." + portName;
              List<ASTConnector> connectorsMatchingSourceAtParent = parentComp.getComponentType()
                .getConnectorsMatchingSource(qNamePort);
              List<ASTConnector> connectorsMatchingSourceAtOwnComp = targetComp.getComponentType()
                .getConnectorsMatchingSource(qNamePort);

              if (connectorsMatchingSourceAtParent.size() == 0
                && connectorsMatchingSourceAtOwnComp.size() == 0) {
                additionalTrafoModels.add(
                  transform(additionalTrafoModels, parentComp, targetComp, false, qNameInstance,
                    qNamePort, portName));
              }
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
    String mockedComponentName =
      TrafoUtil.capitalize(targetComp.getComponentType().getName()) + TrafoUtil.capitalize(port)
        + "Mock";

    ASTMACompilationUnit mockedPortComp;

    boolean isAlreadyCreated = additionalTrafoModels.stream()
      .anyMatch(m -> m.getComponentType().getName().equals(mockedComponentName));
    if (isAlreadyCreated) {
      mockedPortComp = TrafoUtil.getComponentByName(additionalTrafoModels,
        targetComp.getPackage() + "." + mockedComponentName);
    }
    else {
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
    addSubComponentInstantiation(parentComp, fullyQName, mockedComponentName.toLowerCase(),
      createEmptyArguments());

    if (isIngoingPort) {
      addConnection(parentComp, mockedComponentName.toLowerCase() + ".out", qNamePort);
    }
    else {
      addConnection(parentComp, qNamePort, mockedComponentName.toLowerCase() + ".in");
    }

    flagAsGenerated(mockedPortComp);

    return mockedPortComp;
  }

  protected void addBehavior(ASTMACompilationUnit comp, String qNameComp, String portName) {
    List<JsonObject> recordings = dataHandler.getRecordings(qNameComp, portName);

    // create "every" block
    ASTEveryBlockBuilder everyBlock = MontiThingsMill.everyBlockBuilder();
    everyBlock.setSIUnitLiteral(TrafoUtil.createSIUnitLiteral(100, "h"));

    ASTMCJavaBlockBuilder javaBlock = MontiThingsMill.mCJavaBlockBuilder();

    // fill the "every" block with after statements
    for (JsonObject recording : recordings) {
      long timestamp = recording.getJsonNumber("timestamp").longValue();

      String value = recording.getString("msg_content");

      // Determine if a number or a string is present
      boolean isNumeric = true;
      try {
        Integer.parseInt(value);
      }
      catch (NumberFormatException e) {
        isNumeric = false;
      }

      javaBlock.addMCBlockStatement(addAfterBehaviorBlock(timestamp, value, isNumeric));
    }

    everyBlock.setMCJavaBlock(javaBlock.build());
    ASTMTEveryBlockBuilder mtEveryBlock = MontiThingsMill.mTEveryBlockBuilder();
    mtEveryBlock.setEveryBlock(everyBlock.build());
    comp.getComponentType().getBody().addArcElement(mtEveryBlock.build());
  }

  protected ASTAfterStatement addAfterBehaviorBlock(long timestamp, String value,
    boolean isNumeric) {
    ASTAfterStatementBuilder afterStatement = MontiThingsMill.afterStatementBuilder();
    afterStatement.setSIUnitLiteral(TrafoUtil.createSIUnitLiteral(timestamp, "ns"));

    ASTMCJavaBlockBuilder javaBlock = MontiThingsMill.mCJavaBlockBuilder();

    ASTExpressionStatementBuilder astExpressionStatement = MontiThingsMill
      .expressionStatementBuilder();
    ASTAssignmentExpressionBuilder assignmentExpression = MontiThingsMill
      .assignmentExpressionBuilder();

    // implementing " <port> = <value>
    // left side
    ASTNameExpression nameExpression = MontiThingsMill.nameExpressionBuilder().setName("out")
      .build();
    assignmentExpression.setLeft(nameExpression);

    assignmentExpression.setOperator(ASTConstantsAssignmentExpressions.EQUALS);

    // right side
    if (isNumeric) {
      ASTLiteralExpressionBuilder literalExpression = MontiThingsMill.literalExpressionBuilder();

      ASTNatLiteralBuilder natLiteral = MontiThingsMill.natLiteralBuilder();
      natLiteral.setDigits(value);

      literalExpression.setLiteral(natLiteral.build());
      assignmentExpression.setRight(literalExpression.build());
    }
    else {
      ASTNameExpression rightExpression = MontiThingsMill.nameExpressionBuilder().setName(value)
        .build();
      assignmentExpression.setRight(rightExpression);
    }

    astExpressionStatement.setExpression(assignmentExpression.build());
    javaBlock.addMCBlockStatement(astExpressionStatement.build());

    String logContent = "Sending input=" + value.replace("\"", "\\\"") + " on <out> port";
    javaBlock.addMCBlockStatement(createLogStatement(logContent));

    afterStatement.setMCJavaBlock(javaBlock.build());

    return afterStatement.build();
  }
}
