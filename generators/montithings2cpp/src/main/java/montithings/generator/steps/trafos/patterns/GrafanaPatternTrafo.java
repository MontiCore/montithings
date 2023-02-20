package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import behavior._ast.ASTConnectStatement;
import behavior._ast.ASTDisconnectStatement;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTBehavior;
import montithings._visitor.FindConnectionsVisitor;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.TrafoUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GrafanaPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
  private static final String TOOL_NAME = "GrafanaPatternTrafo";
  private static final String INJECTOR_NAME = "Injector";
  private ASTMACompilationUnit injectorComp;

  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                    Collection<ASTMACompilationUnit> addedModels,
                                                    ASTMACompilationUnit targetComp) throws Exception {
    Log.info("Apply transformation to: " + targetComp.getComponentType().getName(), TOOL_NAME);

    Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

    List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

    List<String> qCompInstanceNames = this.getQCompInstanceNames(targetComp, allModels);
    List<String> alreadyTransformed = new ArrayList<>();

    for (FindConnectionsVisitor.Connection connection : this.getConnections(targetComp)) {
      List<String> qCompSourceNames = qCompInstanceNames;
      List<String> qCompTargetNames = qCompInstanceNames;

      if (connection.source.isPresentComponent()) {
        qCompSourceNames = TrafoUtil.getFullyQInstanceName(allModels, targetComp, connection.source.getComponent());
      }

      if (connection.target.isPresentComponent()) {
        qCompTargetNames = TrafoUtil.getFullyQInstanceName(allModels, targetComp, connection.target.getComponent());
      }

      for (String qCompSourceName : qCompSourceNames) {
        for (String qCompTargetName : qCompTargetNames) {
          if (!alreadyTransformed.contains(qCompSourceName + "," + qCompTargetName)) {
            //  Generate Injector Component Type (only once like in Anomaly Detection)
            if (injectorComp == null) {
              injectorComp = createCompilationUnit(targetComp.getPackage(), INJECTOR_NAME);
              additionalTrafoModels.add(injectorComp);
              allModels.add(injectorComp);
            }

            //  Add port to targetComp of Injector Component Type for this connection
            addPort(targetComp, getInPortName(), false, getInjectorPortType(injectorComp));
            addPort(targetComp, getOutPortName(), true, getInjectorPortType(injectorComp));

            // Add behavior block for the new port
            generateBehavior(targetComp, connection.source, connection.target);

            // Set connection as transformed
            alreadyTransformed.add(qCompSourceName + "," + qCompTargetName);
          }
        }
      }
    }

    // TODO: Generate TF here like in Network Minimization

    Log.info("Return " + additionalTrafoModels.size() + " additional trafo models", TOOL_NAME);

    return additionalTrafoModels;
  }

  private ASTMCType getInjectorPortType(ASTMACompilationUnit injectorComp) {
    ASTMCQualifiedName qualifiedName = MontiThingsMill
        .mCQualifiedNameBuilder()
        .addParts("Co" + injectorComp.getComponentType().getName())
        .build();

    return MontiThingsMill
        .mCQualifiedTypeBuilder()
        .setMCQualifiedName(qualifiedName)
        .build();
  }

  private void generateBehavior(ASTMACompilationUnit targetComp, ASTPortAccess portSource, ASTPortAccess portTarget) {
    // On the parent component for each connection insert behavior block to connect with the injector eventually i.e.
    //
    // behavior connectCoName {
    //	inOrig.out -/> outOrig.in;
    //	inOrig.out -> injector.in;
    //	injector.out -> outOrig.in;
    // }
    //
    // behavior disconnectCoName {
    //	inOrig.out -> outOrig.in;
    //	inOrig.out -/> injector.in;
    //	injector.out -/> outOrig.in;
    // }
    //
    // Connect behavior
    ASTDisconnectStatement disconnectSourceToTargetStatement = MontiThingsMill
        .disconnectStatementBuilder()
        .setSource(portSource)
        .setTargetList(Collections.singletonList(portTarget))
        .build();


    ASTPortAccess portInjectorIn = MontiThingsMill
        .portAccessBuilder()
        .setComponent(getInPortName())
        .setPort("in")
        .build();

    ASTPortAccess portInjectorOut = MontiThingsMill
        .portAccessBuilder()
        .setComponent(getOutPortName())
        .setPort("out")
        .build();

    ASTConnector connectorSourceToInjector = MontiThingsMill
        .connectorBuilder()
        .setSource(portSource.getQName())
        .setTargetList(Collections.singletonList(portInjectorIn))
        .build();

    ASTConnectStatement connectSourceToInjectorStatement = MontiThingsMill
        .connectStatementBuilder()
        .setConnector(connectorSourceToInjector)
        .build();

    ASTConnector connectorInjectorToTarget = MontiThingsMill
        .connectorBuilder()
        .setSource(getOutPortName() + ".out")
        .setTargetList(Collections.singletonList(portTarget))
        .build();

    ASTConnectStatement connectInjectorToTargetStatement = MontiThingsMill
        .connectStatementBuilder()
        .setConnector(connectorInjectorToTarget)
        .build();

    ASTMCJavaBlock connectBehaviorBlock = MontiThingsMill
        .mCJavaBlockBuilder()
        .addMCBlockStatement(disconnectSourceToTargetStatement)
        .addMCBlockStatement(connectSourceToInjectorStatement)
        .addMCBlockStatement(connectInjectorToTargetStatement)
        .build();

    ASTBehavior connectBehavior = MontiThingsMill
        .behaviorBuilder()
        .setNamesList(Collections.singletonList(getInPortName()))
        .setMCJavaBlock(connectBehaviorBlock)
        .build();

    targetComp.getComponentType().getBody().addArcElement(connectBehavior);

    // Disconnect behavior
    ASTConnector connectorSourceToTarget = MontiThingsMill
        .connectorBuilder()
        .setSource(portSource.getQName())
        .setTargetList(Collections.singletonList(portTarget))
        .build();

    ASTConnectStatement connectSourceToTargetStatement = MontiThingsMill
        .connectStatementBuilder()
        .setConnector(connectorSourceToTarget)
        .build();

    ASTDisconnectStatement disconnectSourceToInjectorStatement = MontiThingsMill
        .disconnectStatementBuilder()
        .setSource(portSource)
        .setTargetList(Collections.singletonList(portInjectorIn))
        .build();

    ASTDisconnectStatement disconnectInjectorToTargetStatement = MontiThingsMill
        .disconnectStatementBuilder()
        .setSource(portInjectorOut)
        .setTargetList(Collections.singletonList(portTarget))
        .build();

    ASTMCJavaBlock disconnectBehaviorBlock = MontiThingsMill
        .mCJavaBlockBuilder()
        .addMCBlockStatement(connectSourceToTargetStatement)
        .addMCBlockStatement(disconnectSourceToInjectorStatement)
        .addMCBlockStatement(disconnectInjectorToTargetStatement)
        .build();

    ASTBehavior disconnectBehavior = MontiThingsMill
        .behaviorBuilder()
        .setNamesList(Collections.singletonList(getOutPortName()))
        .setMCJavaBlock(disconnectBehaviorBlock)
        .build();

    targetComp.getComponentType().getBody().addArcElement(disconnectBehavior);
  }

  private String getInPortName() {
    return "connect" + "Co" + injectorComp.getComponentType().getName();
  }

  private String getOutPortName() {
    return "disconnect" + "Co" + injectorComp.getComponentType().getName();
  }
}
