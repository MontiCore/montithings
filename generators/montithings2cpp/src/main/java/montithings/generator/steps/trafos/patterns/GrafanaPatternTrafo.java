package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import behavior._ast.ASTConnectStatement;
import behavior._ast.ASTDisconnectStatement;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._ast.ASTBehavior;
import montithings._visitor.FindConnectionsVisitor;
import montithings.generator.data.GeneratorToolState;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.TrafoUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GrafanaPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
  private static final String TOOL_NAME = "GrafanaPatternTrafo";
  private static final String INJECTOR_NAME = "Injector";
  private static final String INJECTOR_IMPL_CPP = "template/patterns/InjectorImplCpp.ftl";
  private static final String INJECTOR_IMPL_HEADER = "template/patterns/InjectorImplHeader.ftl";
  private ASTMACompilationUnit injectorComp;
  private final File modelPath;
  private final File targetHwcPath;
  private final File srcHwcPath;

  public GrafanaPatternTrafo(GeneratorToolState state) {
    this.modelPath = state.getModelPath();
    this.srcHwcPath = state.getHwcPath();
    this.targetHwcPath = Paths.get(state.getTarget().getAbsolutePath(), "hwc").toFile();
  }

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
              ASTMCType portType = this.getPortType(connection.target, targetComp, allModels, this.modelPath);
              injectorComp = getInjectorComp(portType, targetComp);
              additionalTrafoModels.add(injectorComp);
              allModels.add(injectorComp);
            }

            //  Add port to targetComp of Injector Component Type for this connection
            addPort(targetComp, getConnectPortName(), false, getInjectorPortType(injectorComp));
            addPort(targetComp, getDisconnectPortName(), false, getInjectorPortType(injectorComp));

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

  private String mtToPgPortType(String mtPortType) {
    switch (mtPortType) {
      case "int":
      case "double":
      case "float":
        return "float";

      default:
        return "text";
    }
  }

  private ASTMACompilationUnit getInjectorComp(ASTMCType portType, ASTMACompilationUnit targetComp) {
    ASTMACompilationUnit pInjectorComp = createCompilationUnit(targetComp.getPackage(), INJECTOR_NAME, false);
    addPort(pInjectorComp, "in", false, portType);
    addPort(pInjectorComp, "out", true, portType);
    generateInjectorBehavior(pInjectorComp, portType);
    return pInjectorComp;
  }

  private void generateInjectorBehavior(ASTMACompilationUnit comp, ASTMCType portType) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    this.generate(tHwcPath, INJECTOR_NAME + "Impl", ".cpp", INJECTOR_IMPL_CPP,
        comp.getPackage().getQName(), INJECTOR_NAME, mtToPgPortType(portType.toString()));

    this.generate(sHwcPath, INJECTOR_NAME + "Impl", ".cpp", INJECTOR_IMPL_CPP,
        comp.getPackage().getQName(), INJECTOR_NAME, mtToPgPortType(portType.toString()));

    this.generate(tHwcPath, INJECTOR_NAME + "Impl", ".h", INJECTOR_IMPL_HEADER,
        comp.getPackage().getQName(), INJECTOR_NAME);

    this.generate(sHwcPath, INJECTOR_NAME + "Impl", ".h", INJECTOR_IMPL_HEADER,
        comp.getPackage().getQName(), INJECTOR_NAME);
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
        .setComponent(getConnectPortName())
        .setPort("in")
        .build();

    ASTPortAccess portInjectorOut = MontiThingsMill
        .portAccessBuilder()
        .setComponent(getDisconnectPortName())
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
        .setSource(getDisconnectPortName() + ".out")
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
        .setNamesList(Collections.singletonList(getConnectPortName()))
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
        .setNamesList(Collections.singletonList(getDisconnectPortName()))
        .setMCJavaBlock(disconnectBehaviorBlock)
        .build();

    targetComp.getComponentType().getBody().addArcElement(disconnectBehavior);
  }

  private String getConnectPortName() {
    return "connect" + "Co" + injectorComp.getComponentType().getName();
  }

  private String getDisconnectPortName() {
    return "disconnect" + "Co" + injectorComp.getComponentType().getName();
  }

  private void generate(File target, String name, String fileExtension, String template, Object... templateArguments) {
    Path path = Paths.get(target.getAbsolutePath() + File.separator + name + fileExtension);
    Log.debug("Writing to file " + path, "FileGenerator");

    GeneratorSetup setup = new GeneratorSetup();
    setup.setTracing(false);
    GeneratorEngine engine = new GeneratorEngine(setup);
    engine.generateNoA(template, path, templateArguments);
  }
}
