// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTPortAccess;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpressionBuilder;
import de.monticore.expressions.commonexpressions._ast.ASTEqualsExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTArgumentsBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpressionBuilder;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteralBuilder;
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
 * Connections which should be delayed are intercepted by a new component
 * which just forwards the messages, but adds a certain delay.
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
    Log.info("Apply transformation: Delayed Channels: " + targetComp.getComponentType().getName(),
      TOOL_NAME);

    this.additionalTrafoModels = new ArrayList<>();
    List<ASTMACompilationUnit> allModels = new ArrayList<>();
    allModels.addAll(originalModels);
    allModels.addAll(addedModels);

    FindConnectionsVisitor visitor = new FindConnectionsVisitor();
    targetComp.accept(visitor.createTraverser());
    List<FindConnectionsVisitor.Connection> connections = visitor.getConnections();

    // first, collect fully qualified instance names of the target comp
    List<String> qCompInstanceNames = new ArrayList<>();

    for (String parentName : TrafoUtil.findParents(allModels, targetComp)) {
      ASTMACompilationUnit parentComp = TrafoUtil.getComponentByName(allModels, parentName);
      List<ASTComponentInstantiation> instantiations = TrafoUtil
        .getInstantiationsByType(parentComp, targetComp.getComponentType().getName());

      for (ASTComponentInstantiation instantiation : instantiations) {
        for (String instanceName : instantiation.getInstancesNames()) {
          qCompInstanceNames = TrafoUtil.getFullyQInstanceName(allModels, parentComp, instanceName);
        }
      }
    }

    // save already transformed connection pairs
    List<String> alreadyTransformed = new ArrayList<>();

    for (FindConnectionsVisitor.Connection connection : connections) {
      // assume both ports belong to the target component
      List<String> qCompSourceNames = qCompInstanceNames;
      List<String> qCompTargetNames = qCompInstanceNames;

      // otherwise, overwrite them
      if (connection.source.isPresentComponent()) {
        qCompSourceNames = TrafoUtil
          .getFullyQInstanceName(allModels, targetComp, connection.source.getComponent());
      }

      if (connection.target.isPresentComponent()) {
        qCompTargetNames = TrafoUtil
          .getFullyQInstanceName(allModels, targetComp, connection.target.getComponent());
      }

      // iterator over all possible instance combination and create corresponding components
      for (String qCompSourceName : qCompSourceNames) {
        for (String qCompTargetName : qCompTargetNames) {
          if (!alreadyTransformed.contains(qCompSourceName + "," + qCompTargetName)) {
            transform(allModels, targetComp, qCompSourceName, qCompTargetName, connection.source,
              connection.target);
            alreadyTransformed.add(qCompSourceName + "," + qCompTargetName);
          }
        }

      }
    }

    return this.additionalTrafoModels;
  }

  public void transform(Collection<ASTMACompilationUnit> models,
    ASTMACompilationUnit comp,
    String qCompSourceName,
    String qCompTargetName,
    ASTPortAccess portSource,
    ASTPortAccess portTarget) throws Exception {
    // A source or target port can either be declared locally or within a subcomponent.
    String sourceTypeName = TrafoUtil.getPortOwningComponentType(comp, portSource);

    // Name of the added component, e.g. hierarchy.Example.SourceValueSinkValueDelay
    String channelInterceptorComponentName =
      TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) +
        TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) +
        "Delay";

    channelInterceptorComponentName = TrafoUtil.capitalize(channelInterceptorComponentName);

    ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(comp.getPackage());
    fullyQName.addParts(channelInterceptorComponentName);

    // Adds instantiation statement, e.g. "SourceValueSinkValueDelay sourcevaluesinkvaluedelay";
    addSubComponentInstantiation(comp, fullyQName, channelInterceptorComponentName.toLowerCase(),
      createEmptyArguments());

    // Find out the port type. Therefore, first get the component of the source and search for the port.
    // This is only done with the source port as port types have to match anyway
    ASTMCType portType = null;
    try {
      String qName = TrafoUtil.getFullyQNameFromImports(modelPath, comp, sourceTypeName).getQName();
      ASTMACompilationUnit compSource = TrafoUtil.getComponentByName(models, qName);
      portType = TrafoUtil.getPortTypeByName(compSource, portSource.getPort());
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
            portType = TrafoUtil.getPortTypeByName(ifaceComp, portSource.getPort());
          }
          catch (Exception e1) {
            //ignore, check next iface
          }
        }
      }
    }
    if (portType == null) {
      throw new NoSuchElementException(
        "No such port instance found which is named " + portSource.getPort());
    }

    // actually creates the model of the intercepting component
    ASTMACompilationUnit channelInterceptorComponent = createCompilationUnit(comp.getPackage(),
      channelInterceptorComponentName, false);

    List<Long> delays = dataHandler
      .getNetworkDelays(qCompSourceName, portSource.getPort(), qCompTargetName,
        portTarget.getPort());

    addBehavior(channelInterceptorComponent, delays);

    addPort(channelInterceptorComponent,
      "in",
      false,
      portType);
    addPort(channelInterceptorComponent,
      "out",
      true,
      portType);

    flagAsGenerated(channelInterceptorComponent);

    this.additionalTrafoModels.add(channelInterceptorComponent);

    // Replaces the old connection
    removeConnection(comp, portSource, portTarget);
    addConnection(comp, portSource.getQName(),
      channelInterceptorComponentName.toLowerCase() + "." + "in");
    addConnection(comp, channelInterceptorComponentName.toLowerCase() + "." + "out",
      portTarget.getQName());
  }

  void addBehavior(ASTMACompilationUnit comp, List<Long> delays) {
        /*
            int index = 0;

            if (index == 0) {
                delay(299);
            }
            if (index == 1) {
                delay(762);
            }
            if ...

            index = index + 1;
        */
    ASTMCJavaBlockBuilder javaBlockBuilder = MontiThingsMill.mCJavaBlockBuilder();

    // Initiate index variable
    addLongFieldDeclaration(comp, "index", 0);

    //javaBlockBuilder.addMCBlockStatement(createLogStatement("in: $in"));
    int index = 0;
    for (long delay : delays) {
      javaBlockBuilder.addMCBlockStatement(addDelayIfStatement(index, delay));
      index++;
    }

    // implement index += 1;
    javaBlockBuilder.addMCBlockStatement(createIncrementVariableStatement("index"));

    // implement out = in;
    javaBlockBuilder.addMCBlockStatement(createAssignmentStatement("out", "in"));

    ASTBehavior behavior = addEmptyBehavior(comp);
    behavior.setMCJavaBlock(javaBlockBuilder.build());
  }

  private ASTMCBlockStatement addDelayIfStatement(int index, long delay) {
    ASTNameExpression indexNameExpression = MontiThingsMill.nameExpressionBuilder().setName("index")
      .build();

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
    ASTNameExpression delayNameExpression = MontiThingsMill.nameExpressionBuilder()
      .setName("delayNanoseconds").build();

    ASTNatLiteral delayNatLiteral = MontiThingsMill.natLiteralBuilder()
      .setDigits(String.valueOf(delay)).build();
    ASTLiteralExpression delayLiteralExpression = MontiThingsMill.literalExpressionBuilder()
      .setLiteral(delayNatLiteral).build();

    ASTArgumentsBuilder delayArgsBulder = MontiThingsMill.argumentsBuilder();
    delayArgsBulder.addExpression(delayLiteralExpression);

    ASTCallExpressionBuilder thenCallDelayExpressionBuilder = MontiThingsMill
      .callExpressionBuilder();
    thenCallDelayExpressionBuilder.setExpression(delayNameExpression);
    thenCallDelayExpressionBuilder.setArguments(delayArgsBulder.build());
    thenCallDelayExpressionBuilder
      .setName("delayNanoseconds"); // arbitrary value; unclear what this is about

    ASTExpressionStatementBuilder thenExpressionStatementBuilder = MontiThingsMill
      .expressionStatementBuilder();
    thenExpressionStatementBuilder.setExpression(thenCallDelayExpressionBuilder.build());

    ASTMCJavaBlockBuilder thenStatementBuilder = MontiThingsMill.mCJavaBlockBuilder();
    thenStatementBuilder.addMCBlockStatement(thenExpressionStatementBuilder.build());
    thenStatementBuilder
      .addMCBlockStatement(createLogStatement("index=" + index + " | delaying=" + delay));

    ASTIfStatementBuilder ifStatementBuilder = MontiThingsMill.ifStatementBuilder();
    ifStatementBuilder.setCondition(conditionBuilder.build());
    ifStatementBuilder.setThenStatement(thenStatementBuilder.build());
    ifStatementBuilder.setElseStatementAbsent();

    return ifStatementBuilder.build();
  }
}
