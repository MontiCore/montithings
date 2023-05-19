package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTPortAccess;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montithings.MontiThingsMill;
import montithings._visitor.FindConnectionsVisitor;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.TypesHelper;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montiarc._ast.ASTMACompilationUnit;
import de.se_rwth.commons.logging.Log;
import montithings.util.TrafoUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static montithings.generator.helper.FileHelper.getFilesWithEnding;

public class NetworkMinimizationPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
  private static final String TOOL_NAME = "NetworkMinimizationPatternTrafo";
  private static final String UPLOAD_MAYBE_IMPL_CPP = "template/patterns/UploadMaybeImplCpp.ftl";
  private static final String UPLOAD_MAYBE_IMPL_HEADER = "template/patterns/UploadMaybeImplHeader.ftl";
  private static final String DOWNLOAD_MAYBE_IMPL_CPP = "template/patterns/DownloadMaybeImplCpp.ftl";
  private static final String DOWNLOAD_MAYBE_IMPL_HEADER = "template/patterns/DownloadMaybeImplHeader.ftl";
  private static final String BLOB_STORAGE_TF = "template/patterns/BlobStorageTf.ftl";
  private static final String INPORT_NAME = "in";
  private static final String OUTPORT_NAME = "out";
  private static final String PORT_URL_NAME = "url";
  private static final String PORT_URL_TYPE = "String";
  private static final String PORT_DATA_NAME = "orig";
  private static final String MAX_PORT_SIZE = "4000"; // bytes
  private static final String CONTAINERNAME = "pfileuploads";
  private final File modelPath;
  private final File targetHwcPath;
  private final File srcHwcPath;
  private final GeneratorToolState state;

  public NetworkMinimizationPatternTrafo(GeneratorToolState state) {
    this.modelPath = state.getModelPath();
    this.srcHwcPath = state.getHwcPath();
    this.targetHwcPath = Paths.get(state.getTarget().getAbsolutePath(), "hwc").toFile();
    this.state = state;
  }

  @Override
  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                    Collection<ASTMACompilationUnit> addedModels,
                                                    ASTMACompilationUnit targetComp) throws Exception {
    Log.info("Apply Network Minimization Pattern to: " + targetComp.getComponentType().getName(), TOOL_NAME);

    if (isNotSplittedComponent(this.state.getNotSplittedComponents(), targetComp)) {
      Log.info("Component: " + targetComp.getComponentType().getName() + " is marked as not splitted. Stop trafo.", TOOL_NAME);
      return new ArrayList<>();
    }

    Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

    List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

    Map<ASTPortAccess, List<FindConnectionsVisitor.Connection>> componentsToConnections = getComponentsToConnections(targetComp);

    for (Map.Entry<ASTPortAccess, List<FindConnectionsVisitor.Connection>> entry : componentsToConnections.entrySet()) {
      Log.info("Generate wrapper for " + entry.getKey().getQName(), TOOL_NAME);

      // 1. Generate Wrapper for each component
      String compName = entry.getKey().getQName().split("\\.")[0];
      String compWrapperInstanceName = compName + "Wrapper";
      String compWrapperName = TrafoUtil.capitalize(compWrapperInstanceName);
      ASTMACompilationUnit wrapperComp = this.getInterceptComponent(compWrapperName, targetComp, compWrapperInstanceName);
      additionalTrafoModels.add(wrapperComp);
      this.state.addNotSplittedComponent(wrapperComp);
      addSubComponentInstantiation(wrapperComp, getPortFullyQName(entry.getKey(), targetComp, modelPath), compName);

      List<FindConnectionsVisitor.Connection> outConnections = getOutConnections(entry.getKey(), entry.getValue());

      // 2. For each out connection add upload component to wrapper
      for (FindConnectionsVisitor.Connection conn : outConnections) {
        // Create UploadMaybe component inside wrapper
        String baseName = TrafoUtil.replaceDotsWithCamelCase(conn.source.getQName()) + TrafoUtil.replaceDotsWithCamelCase(conn.target.getQName());
        String uploadMaybeName = baseName + "UploadMaybe";
        String uploadMaybeInstanceName = Character.toLowerCase(uploadMaybeName.charAt(0)) + uploadMaybeName.substring(1);
        ASTMACompilationUnit uploadMaybeComp = this.getInterceptComponent(uploadMaybeName, wrapperComp, uploadMaybeInstanceName);
        additionalTrafoModels.add(uploadMaybeComp);
        String portUrlName = Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1) + TrafoUtil.capitalize(PORT_URL_NAME);
        String portDataName = Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1) + TrafoUtil.capitalize(PORT_DATA_NAME);
        this.generateUploadBehavior(uploadMaybeComp, portUrlName, portDataName, uploadMaybeName);

        // Create ports
        ASTMCType portType = this.getPortType(conn.source, targetComp, allModels, this.modelPath);
        String portTypeStr = portTypeToString(portType);

        Log.info("Add new in-port " + INPORT_NAME + " of type " + portTypeStr + " to component " + uploadMaybeName, TOOL_NAME);
        addPort(uploadMaybeComp, INPORT_NAME, false, portType);

        Log.info("Add new out-port " + portUrlName + " of type " + PORT_URL_TYPE + " to component " + uploadMaybeName, TOOL_NAME);
        addPort(uploadMaybeComp, portUrlName, true, this.getUrlPortType());

        Log.info("Add new out-port " + portDataName + " of type " + portTypeStr + " to component " + uploadMaybeName, TOOL_NAME);
        addPort(uploadMaybeComp, portDataName, true, portType);

        Log.info("Add new out-port " + portUrlName + " of type " + PORT_URL_TYPE + " to component " + compWrapperName, TOOL_NAME);
        addPort(wrapperComp, portUrlName, true, this.getUrlPortType());

        Log.info("Add new out-port " + portDataName + " of type " + portTypeStr + " to component " + compWrapperName, TOOL_NAME);
        addPort(wrapperComp, portDataName, true, portType);

        // Create connections
        // A.port -> ABUploadMaybe.in
        Log.info("Add new connection " + conn.source.getQName() + " -> " + uploadMaybeInstanceName + "." + INPORT_NAME +
            " to component " + compWrapperName, TOOL_NAME);
        addConnection(wrapperComp, conn.source.getQName(), uploadMaybeInstanceName + "." + INPORT_NAME);

        // ABUploadMaybe.url -> AWrapperComp.url
        Log.info("Add new connection " + uploadMaybeInstanceName + "." + portUrlName  + " -> " + portUrlName +
            " to component " + compWrapperName, TOOL_NAME);
        addConnection(wrapperComp, uploadMaybeInstanceName + "." + portUrlName, portUrlName);

        // ABUploadMaybe.orig -> AWrapperComp.orig
        Log.info("Add new connection " + uploadMaybeInstanceName + "." + portDataName + " -> " + portDataName +
            " to component " + compWrapperName, TOOL_NAME);
        addConnection(wrapperComp, uploadMaybeInstanceName + "." + portDataName, portDataName);

        if (connectionExists(conn.source, conn.target, getConnections(targetComp))) {
          String targetInstanceName = conn.target.getQName().split("\\.")[0];
          String targetWrapperInstanceName = targetInstanceName + "Wrapper";

          // Create connections
          // AWrapperComp.url -> BWrapperComp.url
          Log.info("Add new connection " + compWrapperInstanceName + "." + portUrlName + " -> " + targetWrapperInstanceName + "." + portUrlName +
              " to component " + targetComp.getComponentType().getName(), TOOL_NAME);
          addConnection(targetComp, compWrapperInstanceName + "." + portUrlName, targetWrapperInstanceName + "." + portUrlName);

          // AWrapperComp.orig -> BWrapperComp.orig
          Log.info("Add new connection " + compWrapperInstanceName + "." + portDataName + " -> " + targetWrapperInstanceName + "." + portDataName +
              " to component " + targetComp.getComponentType().getName(), TOOL_NAME);
          addConnection(targetComp, compWrapperInstanceName + "." + portDataName, targetWrapperInstanceName + "." + portDataName);

          Log.info("Remove existing connection " + conn.source.getQName() + " -> " + conn.target.getQName() +
              " from component " + targetComp.getComponentType(), TOOL_NAME);
          removeConnection(targetComp, conn.source, conn.target);
        }
      }

      List<FindConnectionsVisitor.Connection> inConnections = getInConnections(entry.getKey(), entry.getValue());

      // 3. For each in connection add download component to wrapper
      for (FindConnectionsVisitor.Connection conn : inConnections) {
        // Create DownloadMaybe component inside wrapper
        String baseName = TrafoUtil.replaceDotsWithCamelCase(conn.source.getQName()) + TrafoUtil.replaceDotsWithCamelCase(conn.target.getQName());
        String downloadMaybeName = baseName + "DownloadMaybe";
        String downloadMaybeInstanceName = Character.toLowerCase(downloadMaybeName.charAt(0)) + downloadMaybeName.substring(1);
        ASTMACompilationUnit downloadMaybeComp = this.getInterceptComponent(downloadMaybeName, wrapperComp, downloadMaybeInstanceName);
        additionalTrafoModels.add(downloadMaybeComp);
        String portUrlName = Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1) + TrafoUtil.capitalize(PORT_URL_NAME);
        String portDataName = Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1) + TrafoUtil.capitalize(PORT_DATA_NAME);
        ASTMCType portType = this.getPortType(conn.target, targetComp, allModels, this.modelPath);
        this.generateDownloadBehavior(downloadMaybeComp, portType, portUrlName, portDataName, downloadMaybeName);

        // Create ports
        String portTypeStr = portTypeToString(portType);

        Log.info("Add new out-port " + OUTPORT_NAME + " of type " + portTypeStr + " to component " + downloadMaybeName, TOOL_NAME);
        addPort(downloadMaybeComp, OUTPORT_NAME, true, portType);

        Log.info("Add new in-port " + portUrlName + " of type " + PORT_URL_TYPE + " to component " + downloadMaybeName, TOOL_NAME);
        addPort(downloadMaybeComp, portUrlName, false, this.getUrlPortType());

        Log.info("Add new in-port " + portDataName + " of type " + portTypeStr + " to component " + downloadMaybeName, TOOL_NAME);
        addPort(downloadMaybeComp, portDataName, false, portType);

        Log.info("Add new in-port " + portUrlName + " of type " + PORT_URL_TYPE + " to component " + compWrapperName, TOOL_NAME);
        addPort(wrapperComp, portUrlName, false, this.getUrlPortType());

        Log.info("Add new in-port " + portDataName + " of type " + portTypeStr + " to component " + compWrapperName, TOOL_NAME);
        addPort(wrapperComp, portDataName, false, portType);

        // Create connections
        // ABDownloadMaybe.out -> B.port
        Log.info("Add new connection " + downloadMaybeInstanceName + "." + OUTPORT_NAME + " -> " + conn.target.getQName() +
            " to component " + compWrapperName, TOOL_NAME);
        addConnection(wrapperComp, downloadMaybeInstanceName + "." + OUTPORT_NAME, conn.target.getQName());

        // BWrapper.url -> ABDownloadMaybe.url
        Log.info("Add new connection " + portUrlName  + " -> " + downloadMaybeInstanceName + "." + portUrlName +
            " to component " + compWrapperName, TOOL_NAME);
        addConnection(wrapperComp, portUrlName, downloadMaybeInstanceName + "." + portUrlName);

        // BWrapper.orig -> ABDownloadMaybe.orig
        Log.info("Add new connection " + portDataName + " -> " + downloadMaybeInstanceName + "." + portDataName +
            " to component " + compWrapperName, TOOL_NAME);
        addConnection(wrapperComp, portDataName, downloadMaybeInstanceName + "." + portDataName);

        if (connectionExists(conn.source, conn.target, getConnections(targetComp))) {
          String sourceInstanceName = conn.source.getQName().split("\\.")[0];
          String sourceWrapperInstanceName = sourceInstanceName + "Wrapper";

          // Create connections
          // AWrapperComp.url -> BWrapperComp.url
          Log.info("Add new connection " + sourceWrapperInstanceName + "." + portUrlName + " -> " + compWrapperInstanceName + "." + portUrlName +
              " to component " + targetComp.getComponentType().getName(), TOOL_NAME);
          addConnection(targetComp, sourceWrapperInstanceName + "." + portUrlName, compWrapperInstanceName + "." + portUrlName);

          // AWrapperComp.orig -> BWrapperComp.orig
          Log.info("Add new connection " + sourceWrapperInstanceName + "." + portDataName + " -> " + compWrapperInstanceName + "." + portDataName +
              " to component " + targetComp.getComponentType().getName(), TOOL_NAME);
          addConnection(targetComp, sourceWrapperInstanceName + "." + portDataName, compWrapperInstanceName + "." + portDataName);

          Log.info("Remove existing connection " + conn.source.getQName() + " -> " + conn.target.getQName() +
              " from component " + targetComp.getComponentType(), TOOL_NAME);
          removeConnection(targetComp, conn.source, conn.target);
        }
      }

      // 4. Remove original component instantiation from target comp
      removeSubcomponentInstantiation(targetComp, Collections.singletonList(compName));
    }

    if (additionalTrafoModels.size() > 0 && !this.state.getHasBlobStorageTf()) {
      // Generate blob storage tf only once for the first component that requires it
      // This works, because for all other components the container sas is inserted as env var by deploy mgr
      this.generateTf(targetComp);
      this.state.setHasBlobStorageTf(true);
    }

    Log.info("Return " + additionalTrafoModels.size() + " additional trafo models", TOOL_NAME);
    
    return additionalTrafoModels;
  }

  private Map<ASTPortAccess, List<FindConnectionsVisitor.Connection>> getComponentsToConnections(ASTMACompilationUnit comp) {
    Map<ASTPortAccess, List<FindConnectionsVisitor.Connection>> componentsToConnections = new HashMap<>();

    List<FindConnectionsVisitor.Connection> connections = this.getConnections(comp);

    for (FindConnectionsVisitor.Connection conn : connections) {
      if (componentsToConnections.size() == 0) {
        componentsToConnections.put(conn.source, Collections.singletonList(conn));
        componentsToConnections.put(conn.target, Collections.singletonList(conn));
      } else {
        boolean insertedSource = false;
        boolean insertedTarget = false;

        for (Map.Entry<ASTPortAccess, List<FindConnectionsVisitor.Connection>> entry : componentsToConnections.entrySet()) {
          if (entry.getKey().getComponent().equals(conn.source.getComponent())) {
            List<FindConnectionsVisitor.Connection> componentConnections = new ArrayList<>(entry.getValue());
            componentConnections.add(conn);
            componentsToConnections.put(entry.getKey(), componentConnections);
            insertedSource = true;
          }
          if (entry.getKey().getComponent().equals(conn.target.getComponent())) {
            List<FindConnectionsVisitor.Connection> componentConnections = new ArrayList<>(entry.getValue());
            componentConnections.add(conn);
            componentsToConnections.put(entry.getKey(), componentConnections);
            insertedTarget = true;
          }
        }

        if (!insertedSource) {
          componentsToConnections.put(conn.source, Collections.singletonList(conn));
        }

        if (!insertedTarget) {
          componentsToConnections.put(conn.target, Collections.singletonList(conn));
        }
      }
    }

    return componentsToConnections;
  }

  private String portTypeToString(ASTMCType portType) {
    return new MontiThingsFullPrettyPrinter().prettyprint(portType);
  }

  private boolean connectionExists(ASTPortAccess portSource, ASTPortAccess portTarget, List<FindConnectionsVisitor.Connection> connections) {
    for (FindConnectionsVisitor.Connection connection : connections) {
      if (connection.target.isPresentComponent() && connection.source.isPresentComponent() &&
          connection.source.getQName().equals(portSource.getQName()) && connection.target.getQName().equals(portTarget.getQName())) {
        return true;
      }
    }
    return false;
  }

  private List<FindConnectionsVisitor.Connection> getInConnections(ASTPortAccess portSource, List<FindConnectionsVisitor.Connection> connections) {
    List<FindConnectionsVisitor.Connection> inConnections = new ArrayList<>();

    for (FindConnectionsVisitor.Connection connection : connections) {
      if (connection.target.isPresentComponent()) {
        String targetInstanceName = connection.target.getQName().split("\\.")[0];
        String portSourceInstanceName = portSource.getQName().split("\\.")[0];
        if (targetInstanceName.equals(portSourceInstanceName)) {
          inConnections.add(connection);
        }
      }
    }

    return inConnections;
  }

  private List<FindConnectionsVisitor.Connection> getOutConnections(ASTPortAccess portTarget, List<FindConnectionsVisitor.Connection> connections) {
    List<FindConnectionsVisitor.Connection> outConnections = new ArrayList<>();

    for (FindConnectionsVisitor.Connection connection : connections) {
      if (connection.source.isPresentComponent()) {
        String sourceInstanceName = connection.source.getQName().split("\\.")[0];
        String portTargetInstanceName = portTarget.getQName().split("\\.")[0];
        if (sourceInstanceName.equals(portTargetInstanceName)) {
          outConnections.add(connection);
        }
      }
    }

    return outConnections;
  }

  private ASTMCType getUrlPortType() {
    ASTMCQualifiedName qualifiedName = MontiThingsMill.mCQualifiedNameBuilder().addParts(PORT_URL_TYPE).build();
    return MontiThingsMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualifiedName).build();
  }

  private void generateUploadBehavior(ASTMACompilationUnit comp, String portUrlName, String portDataName, String fileName) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    ASTMCQualifiedName fullyQName = TrafoUtil.copyASTMCQualifiedName(comp.getPackage());
    fullyQName.addParts(comp.getComponentType().getName());
    String containerName = CONTAINERNAME + "/" + fullyQName.getQName().replaceAll("\\.", "").toLowerCase();

    this.generate(tHwcPath, fileName + "Impl", ".cpp", UPLOAD_MAYBE_IMPL_CPP,
            comp.getPackage().getQName(), fileName, MAX_PORT_SIZE, containerName, portUrlName, portDataName, INPORT_NAME);

    this.generate(sHwcPath, fileName + "Impl", ".cpp", UPLOAD_MAYBE_IMPL_CPP,
            comp.getPackage().getQName(), fileName, MAX_PORT_SIZE, containerName, portUrlName, portDataName, INPORT_NAME);

    this.generate(tHwcPath, fileName + "Impl", ".h", UPLOAD_MAYBE_IMPL_HEADER,
            comp.getPackage().getQName(), fileName);

    this.generate(sHwcPath, fileName + "Impl", ".h", UPLOAD_MAYBE_IMPL_HEADER,
            comp.getPackage().getQName(), fileName);
  }

  private void generateDownloadBehavior(ASTMACompilationUnit comp, ASTMCType portType, String portUrlName, String portDataName, String fileName) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    String portTypeStr = TypesHelper.java2cppTypeString(new MontiThingsFullPrettyPrinter().prettyprint(portType));

    this.generate(tHwcPath, fileName + "Impl", ".cpp", DOWNLOAD_MAYBE_IMPL_CPP,
            comp.getPackage().getQName(), fileName, portUrlName, portDataName, OUTPORT_NAME, portTypeStr);

    this.generate(sHwcPath, fileName + "Impl", ".cpp", DOWNLOAD_MAYBE_IMPL_CPP,
            comp.getPackage().getQName(), fileName, portUrlName, portDataName, OUTPORT_NAME, portTypeStr);

    this.generate(tHwcPath, fileName + "Impl", ".h", DOWNLOAD_MAYBE_IMPL_HEADER,
            comp.getPackage().getQName(), fileName);

    this.generate(sHwcPath, fileName + "Impl", ".h", DOWNLOAD_MAYBE_IMPL_HEADER,
            comp.getPackage().getQName(), fileName);
  }

  private void generateTf(ASTMACompilationUnit comp) throws IOException {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    if (hasTf(comp)) {
      Log.info("Comp " + comp.getComponentType().getName() + " already has .tf file, append to existing file", TOOL_NAME);

      this.generate(tHwcPath, comp.getComponentType().getName() + "Copy", ".tf", BLOB_STORAGE_TF, CONTAINERNAME);
      this.mergeTf(tHwcPath, comp);

      this.generate(sHwcPath, comp.getComponentType().getName() + "Copy", ".tf", BLOB_STORAGE_TF, CONTAINERNAME);
      this.mergeTf(sHwcPath, comp);

      return;
    }

    this.generate(tHwcPath, comp.getComponentType().getName(), ".tf", BLOB_STORAGE_TF, CONTAINERNAME);
    this.generate(sHwcPath, comp.getComponentType().getName(), ".tf", BLOB_STORAGE_TF, CONTAINERNAME);
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
