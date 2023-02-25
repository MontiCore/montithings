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
import java.util.*;

public class GrafanaPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
  private static final String TOOL_NAME = "GrafanaPatternTrafo";
  private static final String INJECTOR_IF_NAME = "InjectorIF";
  private static final String INJECTOR_NAME = "Injector";
  private static final String INJECTOR_IMPL_CPP = "template/patterns/InjectorImplCpp.ftl";
  private static final String INJECTOR_IMPL_HEADER = "template/patterns/InjectorImplHeader.ftl";
  private static final String TF = "template/patterns/GrafanaPatternTf.ftl";
  private final String grafanaInstanceUrl;
  private final String grafanaApiKey;
  private final File modelPath;
  private final File targetHwcPath;
  private final File srcHwcPath;
  private final GeneratorToolState state;

  public GrafanaPatternTrafo(GeneratorToolState state, String grafanaInstanceUrl, String grafanaApiKey) {
    this.modelPath = state.getModelPath();
    this.srcHwcPath = state.getHwcPath();
    this.targetHwcPath = Paths.get(state.getTarget().getAbsolutePath(), "hwc").toFile();
    this.state = state;
    this.grafanaInstanceUrl = grafanaInstanceUrl;
    this.grafanaApiKey = grafanaApiKey;
  }

  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                    Collection<ASTMACompilationUnit> addedModels,
                                                    ASTMACompilationUnit targetComp) throws Exception {
    Log.info("Apply transformation to: " + targetComp.getComponentType().getName(), TOOL_NAME);

    Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

    List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

    List<String> qCompInstanceNames = this.getQCompInstanceNames(targetComp, allModels);
    List<String> alreadyTransformed = new ArrayList<>();

    List<GrafanaPanel> panels = new ArrayList<>();

    for (FindConnectionsVisitor.Connection connection : this.getConnections(targetComp)) {
      List<String> qCompSourceNames = qCompInstanceNames;
      List<String> qCompTargetNames = qCompInstanceNames;

      if (connection.source.isPresentComponent()) {
        qCompSourceNames = TrafoUtil.getFullyQInstanceName(allModels, targetComp, connection.source.getComponent());
      }

      if (connection.target.isPresentComponent()) {
        qCompTargetNames = TrafoUtil.getFullyQInstanceName(allModels, targetComp, connection.target.getComponent());
      }

      int x = 0;
      int y = 0;

      for (String qCompSourceName : qCompSourceNames) {
        for (String qCompTargetName : qCompTargetNames) {
          if (!alreadyTransformed.contains(qCompSourceName + "," + qCompTargetName)) {
              ASTMCType portType = this.getPortType(connection.target, targetComp, allModels, this.modelPath);
              ASTMACompilationUnit injectorIF = getInjectorIF(portType, targetComp, qCompSourceName, qCompTargetName);
              ASTMACompilationUnit injectorComp = getInjectorComp(portType, targetComp, qCompSourceName, qCompTargetName);
              additionalTrafoModels.add(injectorIF);
              additionalTrafoModels.add(injectorComp);
              allModels.add(injectorIF);
              allModels.add(injectorComp);

            //  Add port to targetComp of Injector Component Type for this connection
            addPort(targetComp, getConnectPortName(injectorIF), false, getInjectorPortType(injectorIF));
            addPort(targetComp, getDisconnectPortName(injectorIF), false, getInjectorPortType(injectorIF));

            // Add behavior block for the new port
            generateBehavior(targetComp, connection.source, connection.target, injectorIF);

            String panelTitle = TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) + TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) + INJECTOR_IF_NAME;
            panels.add(new GrafanaPanel(x, y, panelTitle, panelTitle));

            // Set connection as transformed
            alreadyTransformed.add(qCompSourceName + "," + qCompTargetName);
          }
        }
      }
    }

    // Setup grafana provider and postgres db only once
    // Setup dashboard for each component
    if (additionalTrafoModels.size() > 0) {
      this.generateTf(targetComp, panels, targetComp.getComponentType().getName(), !this.state.getHasGrafanaTf());

      if (!this.state.getHasGrafanaTf()) {
        this.state.setHasGrafanaTf(true);
      }
    }

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
      case "boolean":
        return "boolean";

      case "int":
      case "double":
      case "float":
        return "float";

      default:
        return "text";
    }
  }

  private ASTMACompilationUnit getInjectorIF(ASTMCType portType, ASTMACompilationUnit targetComp, String qCompSourceName,
                                             String qCompTargetName) {
    String injectorIFName = TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) + TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) + INJECTOR_IF_NAME;
    ASTMACompilationUnit pInjectorComp = createCompilationUnit(targetComp.getPackage(), injectorIFName, true);
    addPort(pInjectorComp, "in", false, portType);
    addPort(pInjectorComp, "out", true, portType);
    return pInjectorComp;
  }

  private ASTMACompilationUnit getInjectorComp(ASTMCType portType, ASTMACompilationUnit targetComp, String qCompSourceName,
                                               String qCompTargetName) {
    String injectorName = TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) + TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) + INJECTOR_NAME;
    String injectorIFName = TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) + TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) + INJECTOR_IF_NAME;
    ASTMACompilationUnit pInjectorComp = createCompilationUnit(targetComp.getPackage(), injectorName, false, Optional.of(injectorIFName));
    addPort(pInjectorComp, "in", false, portType);
    addPort(pInjectorComp, "out", true, portType);
    generateInjectorBehavior(pInjectorComp, portType, injectorName);
    return pInjectorComp;
  }

  private void generateInjectorBehavior(ASTMACompilationUnit comp, ASTMCType portType, String injectorName) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    this.generate(tHwcPath, injectorName + "Impl", ".cpp", INJECTOR_IMPL_CPP,
        comp.getPackage().getQName(), injectorName, mtToPgPortType(portType.toString()));

    this.generate(sHwcPath, injectorName + "Impl", ".cpp", INJECTOR_IMPL_CPP,
        comp.getPackage().getQName(), injectorName, mtToPgPortType(portType.toString()));

    this.generate(tHwcPath, injectorName + "Impl", ".h", INJECTOR_IMPL_HEADER,
        comp.getPackage().getQName(), injectorName);

    this.generate(sHwcPath, injectorName + "Impl", ".h", INJECTOR_IMPL_HEADER,
        comp.getPackage().getQName(), injectorName);
  }


  private void generateTf(ASTMACompilationUnit comp, List<GrafanaPanel> panels, String dashboardTitle, boolean setupProvider) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    this.generate(tHwcPath, comp.getComponentType().getName(), ".tf", TF, grafanaInstanceUrl, grafanaApiKey, panels, dashboardTitle, setupProvider);
    this.generate(sHwcPath, comp.getComponentType().getName(), ".tf", TF, grafanaInstanceUrl, grafanaApiKey, panels, dashboardTitle, setupProvider);
  }

  private void generateBehavior(ASTMACompilationUnit targetComp, ASTPortAccess portSource, ASTPortAccess portTarget, ASTMACompilationUnit injectorIF) {
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

    ASTPortAccess connectPortInjectorIn = MontiThingsMill
        .portAccessBuilder()
        .setComponent(getConnectPortName(injectorIF))
        .setPort("in")
        .build();

    ASTPortAccess disconnectPortInjectorIn = MontiThingsMill
        .portAccessBuilder()
        .setComponent(getDisconnectPortName(injectorIF))
        .setPort("in")
        .build();

    ASTPortAccess disconnectPortInjectorOut = MontiThingsMill
        .portAccessBuilder()
        .setComponent(getDisconnectPortName(injectorIF))
        .setPort("out")
        .build();

    ASTConnector connectorSourceToInjector = MontiThingsMill
        .connectorBuilder()
        .setSource(portSource.getQName())
        .setTargetList(Collections.singletonList(connectPortInjectorIn))
        .build();

    ASTConnectStatement connectSourceToInjectorStatement = MontiThingsMill
        .connectStatementBuilder()
        .setConnector(connectorSourceToInjector)
        .build();

    ASTConnector connectorInjectorToTarget = MontiThingsMill
        .connectorBuilder()
        .setSource(getConnectPortName(injectorIF) + ".out")
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
        .setNamesList(Collections.singletonList(getConnectPortName(injectorIF)))
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
        .setTargetList(Collections.singletonList(disconnectPortInjectorIn))
        .build();

    ASTDisconnectStatement disconnectInjectorToTargetStatement = MontiThingsMill
        .disconnectStatementBuilder()
        .setSource(disconnectPortInjectorOut)
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
        .setNamesList(Collections.singletonList(getDisconnectPortName(injectorIF)))
        .setMCJavaBlock(disconnectBehaviorBlock)
        .build();

    targetComp.getComponentType().getBody().addArcElement(disconnectBehavior);
  }

  private String getConnectPortName(ASTMACompilationUnit injectorIF) {
    return "connect" + "Co" + injectorIF.getComponentType().getName();
  }

  private String getDisconnectPortName(ASTMACompilationUnit injectorIF) {
    return "disconnect" + "Co" + injectorIF.getComponentType().getName();
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
