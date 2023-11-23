// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTConnector;
import arcbasis._ast.ASTPortAccess;
import behavior._ast.ASTConnectStatement;
import behavior._ast.ASTDisconnectStatement;
import behavior._ast.ASTLogStatement;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlockBuilder;
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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static montithings.generator.helper.FileHelper.getFilesWithEnding;

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
    Log.info("Apply Grafana Pattern to: " + targetComp.getComponentType().getName(), TOOL_NAME);

    if (isNotSplittedComponent(this.state.getNotSplittedComponents(), targetComp)) {
      Log.info("Component: " + targetComp.getComponentType().getName() + " is marked as not splitted. Stop trafo.", TOOL_NAME);
      return new ArrayList<>();
    }

    Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

    List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

    Map<String, List<FindConnectionsVisitor.Connection>> sameCompConnections = new HashMap<>();
    List<String> qCompInstanceNames = this.getQCompInstanceNames(targetComp, allModels);
    List<String> alreadyTransformed = new ArrayList<>();

    List<GrafanaPanel> panels = new ArrayList<>();

    // Step 1: Group connections together that start in same component and end in same component
    // E.g. A.out1 -> B.in1 and A.out2 -> B.in2 are grouped together by key "AB"
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
          String key = qCompSourceName + qCompTargetName;

          if (sameCompConnections.containsKey(key)) {
            List<FindConnectionsVisitor.Connection> oldConnections = new ArrayList<>(sameCompConnections.get(key));
            oldConnections.add(connection);
            sameCompConnections.put(key, oldConnections);
          } else {
            sameCompConnections.put(key, Collections.singletonList(connection));
          }
        }
      }
    }

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
              List<FindConnectionsVisitor.Connection> connections = sameCompConnections.get(qCompSourceName + qCompTargetName);

              ASTMACompilationUnit injectorIF = getInjectorIF(targetComp, qCompSourceName, qCompTargetName, connections, allModels);
              ASTMACompilationUnit injectorComp = getInjectorComp( targetComp, qCompSourceName, qCompTargetName, connections, allModels);
              additionalTrafoModels.add(injectorIF);
              additionalTrafoModels.add(injectorComp);
              allModels.add(injectorIF);
              allModels.add(injectorComp);

            //  Add port to targetComp of Injector Component Type for this connection
            addPort(targetComp, getConnectPortName(injectorIF), false, getInjectorPortType(injectorIF));
            addPort(targetComp, getDisconnectPortName(injectorIF), false, getInjectorPortType(injectorIF));

            // Add behavior block for the new port
            generateBehavior(targetComp, connections, injectorIF);

            String panelTitle = TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) + TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) + INJECTOR_IF_NAME;
            panels.add(new GrafanaPanel(x, y, panelTitle, getTableName(panelTitle)));

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

  private ASTMACompilationUnit getInjectorIF(ASTMACompilationUnit targetComp, String qCompSourceName, String qCompTargetName,
                                             List<FindConnectionsVisitor.Connection> connections, List<ASTMACompilationUnit> allModels) throws Exception {
    String injectorIFName = TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) + TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) + INJECTOR_IF_NAME;
    ASTMACompilationUnit pInjectorComp = createCompilationUnit(targetComp.getPackage(), injectorIFName, true);

    for (FindConnectionsVisitor.Connection connection : connections) {
      ASTMCType portType = this.getPortType(connection.target, targetComp, allModels, this.modelPath);
      String inPortName = "in" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(connection.source.getQName()));
      String outPortName = "out" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(connection.target.getQName()));
      addPort(pInjectorComp, inPortName, false, portType);
      addPort(pInjectorComp, outPortName, true, portType);
    }

    return pInjectorComp;
  }

  private ASTMACompilationUnit getInjectorComp(ASTMACompilationUnit targetComp, String qCompSourceName, String qCompTargetName,
                                               List<FindConnectionsVisitor.Connection> connections, List<ASTMACompilationUnit> allModels) throws Exception {
    String injectorName = TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) + TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) + INJECTOR_NAME;
    String injectorIFName = TrafoUtil.replaceDotsWithCamelCase(qCompSourceName) + TrafoUtil.replaceDotsWithCamelCase(qCompTargetName) + INJECTOR_IF_NAME;
    ASTMACompilationUnit pInjectorComp = createCompilationUnit(targetComp.getPackage(), injectorName, false, Optional.of(injectorIFName));

    List<String> inPortNames = new ArrayList<>();
    List<String> outPortNames = new ArrayList<>();
    List<ASTMCType> portTypes = new ArrayList<>();

    for (FindConnectionsVisitor.Connection connection : connections) {
      ASTMCType portType = this.getPortType(connection.target, targetComp, allModels, this.modelPath);
      String inPortName = "in" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(connection.source.getQName()));
      String outPortName = "out" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(connection.target.getQName()));
      addPort(pInjectorComp, inPortName, false, portType);
      addPort(pInjectorComp, outPortName, true, portType);
      portTypes.add(portType);
      inPortNames.add(inPortName);
      outPortNames.add(outPortName);
    }

    generateInjectorBehavior(pInjectorComp, injectorName, inPortNames, outPortNames, portTypes);
    return pInjectorComp;
  }

  private void generateInjectorBehavior(ASTMACompilationUnit comp, String injectorName, List<String> inPortNames,
                                        List<String> outPortNames, List<ASTMCType> portTypes) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    this.generate(tHwcPath, injectorName + "Impl", ".cpp", INJECTOR_IMPL_CPP,
        comp.getPackage().getQName(), injectorName, inPortNames, outPortNames,
        portTypes.stream().map(p -> mtToPgPortType(p.toString())).toArray(),
        getTableName(injectorName));

    this.generate(sHwcPath, injectorName + "Impl", ".cpp", INJECTOR_IMPL_CPP,
        comp.getPackage().getQName(),  injectorName, inPortNames, outPortNames,
        portTypes.stream().map(p -> mtToPgPortType(p.toString())).toArray(),
        getTableName(injectorName));

    this.generate(tHwcPath, injectorName + "Impl", ".h", INJECTOR_IMPL_HEADER,
        comp.getPackage().getQName(), injectorName);

    this.generate(sHwcPath, injectorName + "Impl", ".h", INJECTOR_IMPL_HEADER,
        comp.getPackage().getQName(), injectorName);
  }

  private String getTableName(String compName) {
    // PG Table names must have length <= 63
    // As we use table name with suffix _pk in ftl, cap at 60
    String tableCamelCase = compName.substring(0, Math.min(compName.length(), 60));
    return tableCamelCase.toLowerCase();
  }

  private void generateTf(ASTMACompilationUnit comp, List<GrafanaPanel> panels, String dashboardTitle, boolean setupProvider) throws IOException {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    if (hasTf(comp)) {
      Log.info("Comp " + comp.getComponentType().getName() + " already has .tf file, append to existing file", TOOL_NAME);

      this.generate(tHwcPath, comp.getComponentType().getName() + "Copy", ".tf", TF, grafanaInstanceUrl, grafanaApiKey, panels, dashboardTitle, setupProvider);
      this.mergeTf(tHwcPath, comp);

      this.generate(sHwcPath, comp.getComponentType().getName() + "Copy", ".tf", TF, grafanaInstanceUrl, grafanaApiKey, panels, dashboardTitle, setupProvider);
      this.mergeTf(sHwcPath, comp);

      return;
    }

    this.generate(tHwcPath, comp.getComponentType().getName(), ".tf", TF, grafanaInstanceUrl, grafanaApiKey, panels, dashboardTitle, setupProvider);
    this.generate(sHwcPath, comp.getComponentType().getName(), ".tf", TF, grafanaInstanceUrl, grafanaApiKey, panels, dashboardTitle, setupProvider);
  }

  private void generateBehavior(ASTMACompilationUnit targetComp, List<FindConnectionsVisitor.Connection> connections, ASTMACompilationUnit injectorIF) {
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
    ASTLogStatement connectLogStatement = MontiThingsMill.logStatementBuilder().setStringLiteral(
        MontiThingsMill.stringLiteralBuilder().setSource("Connect " + getConnectPortName(injectorIF)).build()
    ).build();

    ASTMCJavaBlockBuilder b = MontiThingsMill.mCJavaBlockBuilder();
    b.addMCBlockStatement(connectLogStatement);

    for (FindConnectionsVisitor.Connection connection : connections) {
      String inPortName = "in" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(connection.source.getQName()));
      String outPortName = "out" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(connection.target.getQName()));

      ASTDisconnectStatement disconnectSourceToTargetStatement = MontiThingsMill
          .disconnectStatementBuilder()
          .setSource(connection.source)
          .setTargetList(Collections.singletonList(connection.target))
          .build();

      ASTPortAccess connectPortInjectorIn = MontiThingsMill
          .portAccessBuilder()
          .setComponent(getConnectPortName(injectorIF))
          .setPort(inPortName)
          .build();

      ASTConnector connectorSourceToInjector = MontiThingsMill
          .connectorBuilder()
          .setSource(connection.source.getQName())
          .setTargetList(Collections.singletonList(connectPortInjectorIn))
          .build();

      ASTConnectStatement connectSourceToInjectorStatement = MontiThingsMill
          .connectStatementBuilder()
          .setConnector(connectorSourceToInjector)
          .build();

      ASTConnector connectorInjectorToTarget = MontiThingsMill
          .connectorBuilder()
          .setSource(getConnectPortName(injectorIF) + "." + outPortName)
          .setTargetList(Collections.singletonList(connection.target))
          .build();

      ASTConnectStatement connectInjectorToTargetStatement = MontiThingsMill
          .connectStatementBuilder()
          .setConnector(connectorInjectorToTarget)
          .build();

      b.addMCBlockStatement(disconnectSourceToTargetStatement)
          .addMCBlockStatement(connectSourceToInjectorStatement)
          .addMCBlockStatement(connectInjectorToTargetStatement);
    }

    ASTBehavior connectBehavior = MontiThingsMill.behaviorBuilder()
        .setNamesList(Collections.singletonList(getConnectPortName(injectorIF)))
        .setMCJavaBlock(b.build())
        .build();

    targetComp.getComponentType().getBody().addArcElement(connectBehavior);


    ASTLogStatement disconnectLogStatement = MontiThingsMill.logStatementBuilder().setStringLiteral(
        MontiThingsMill.stringLiteralBuilder().setSource("Disconnect " + getDisconnectPortName(injectorIF)).build()
    ).build();

    b = MontiThingsMill.mCJavaBlockBuilder();
    b.addMCBlockStatement(disconnectLogStatement);

    for (FindConnectionsVisitor.Connection connection : connections) {
      String inPortName = "in" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(connection.source.getQName()));
      String outPortName = "out" + TrafoUtil.capitalize(TrafoUtil.replaceDotsWithCamelCase(connection.target.getQName()));

      ASTPortAccess disconnectPortInjectorIn = MontiThingsMill
          .portAccessBuilder()
          .setComponent(getDisconnectPortName(injectorIF))
          .setPort(inPortName)
          .build();

      ASTPortAccess disconnectPortInjectorOut = MontiThingsMill
          .portAccessBuilder()
          .setComponent(getDisconnectPortName(injectorIF))
          .setPort(outPortName)
          .build();

      ASTConnector connectorSourceToTarget = MontiThingsMill
          .connectorBuilder()
          .setSource(connection.source.getQName())
          .setTargetList(Collections.singletonList(connection.target))
          .build();

      ASTConnectStatement connectSourceToTargetStatement = MontiThingsMill
          .connectStatementBuilder()
          .setConnector(connectorSourceToTarget)
          .build();

      ASTDisconnectStatement disconnectSourceToInjectorStatement = MontiThingsMill
          .disconnectStatementBuilder()
          .setSource(connection.source)
          .setTargetList(Collections.singletonList(disconnectPortInjectorIn))
          .build();

      ASTDisconnectStatement disconnectInjectorToTargetStatement = MontiThingsMill
          .disconnectStatementBuilder()
          .setSource(disconnectPortInjectorOut)
          .setTargetList(Collections.singletonList(connection.target))
          .build();

      b.addMCBlockStatement(connectSourceToTargetStatement)
          .addMCBlockStatement(disconnectSourceToInjectorStatement)
          .addMCBlockStatement(disconnectInjectorToTargetStatement);
    }

    ASTBehavior disconnectBehavior = MontiThingsMill.behaviorBuilder()
        .setNamesList(Collections.singletonList(getDisconnectPortName(injectorIF)))
        .setMCJavaBlock(b.build())
        .build();

    targetComp.getComponentType().getBody().addArcElement(disconnectBehavior);
  }

  private String getConnectPortName(ASTMACompilationUnit injectorIF) {
    return "connect" + "Co" + injectorIF.getComponentType().getName();
  }

  private String getDisconnectPortName(ASTMACompilationUnit injectorIF) {
    return "disconnect" + "Co" + injectorIF.getComponentType().getName();
  }

  private boolean hasTf(ASTMACompilationUnit comp) {
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    Set<String> tfFiles = getFilesWithEnding(sHwcPath, Stream.of(".tf").collect(Collectors.toSet()));

    for (String tfFilename : tfFiles) {
      if (tfFilename.equals(comp.getComponentType().getName())) {
        return true;
      }
    }

    return false;
  }

  private void mergeTf(File hwcPath, ASTMACompilationUnit comp) throws IOException {
    Path toAppend = Paths.get(hwcPath.getAbsolutePath() + File.separator + comp.getComponentType().getName() + "Copy.tf");
    Path orig = Paths.get(hwcPath.getAbsolutePath() + File.separator + comp.getComponentType().getName() + ".tf");

    List<String> lines = Files.readAllLines(toAppend, StandardCharsets.UTF_8);
    Files.write(orig, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

    Files.delete(toAppend);
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
