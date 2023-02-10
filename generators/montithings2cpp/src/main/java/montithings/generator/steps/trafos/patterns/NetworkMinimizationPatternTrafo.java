package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTPortAccess;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montithings.MontiThingsMill;
import montithings._visitor.FindConnectionsVisitor;
import montithings.generator.data.GeneratorToolState;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montiarc._ast.ASTMACompilationUnit;
import de.se_rwth.commons.logging.Log;
import montithings.util.TrafoUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class NetworkMinimizationPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
  private static final String TOOL_NAME = "NetworkMinimizationPatternTrafo";
  private static final String UPLOAD_MAYBE_NAME = "UploadMaybe";
  private static final String UPLOAD_MAYBE_WRAPPER_NAME = "UploadMaybeWrapper";
  private static final String DOWNLOAD_MAYBE_NAME = "DownloadMaybe";
  private static final String DOWNLOAD_MAYBE_WRAPPER_NAME = "DownloadMaybeWrapper";
  private static final String UPLOAD_MAYBE_IMPL_CPP = "template/patterns/UploadMaybeImplCpp.ftl";
  private static final String UPLOAD_MAYBE_IMPL_HEADER = "template/patterns/UploadMaybeImplHeader.ftl";
  private static final String DOWNLOAD_MAYBE_IMPL_CPP = "template/patterns/DownloadMaybeImplCpp.ftl";
  private static final String DOWNLOAD_MAYBE_IMPL_HEADER = "template/patterns/DownloadMaybeImplHeader.ftl";
  private static final String UPLOAD_MAYBE_WRAPPER_MTCFG = "template/patterns/UploadMaybeWrapperMtcfg.ftl";
  private static final String DOWNLOAD_MAYBE_WRAPPER_MTCFG = "template/patterns/DownloadMaybeWrapperMtcfg.ftl";
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
            // Generate interceptor components for up- and download wrapper
            ASTMACompilationUnit uploadMaybeWrapperComp = this.getInterceptComponent(UPLOAD_MAYBE_WRAPPER_NAME, targetComp);
            ASTMACompilationUnit downloadMaybeWrapperComp = this.getInterceptComponent(DOWNLOAD_MAYBE_WRAPPER_NAME, targetComp);
            additionalTrafoModels.add(uploadMaybeWrapperComp);
            additionalTrafoModels.add(downloadMaybeWrapperComp);
            this.state.addNotSplittedComponent(uploadMaybeWrapperComp);
            this.state.addNotSplittedComponent(downloadMaybeWrapperComp);

            // Generate interceptor components for up- and download
            ASTMACompilationUnit uploadMaybeComp = this.getInterceptComponent(UPLOAD_MAYBE_NAME, uploadMaybeWrapperComp);
            ASTMACompilationUnit downloadMaybeComp = this.getInterceptComponent(DOWNLOAD_MAYBE_NAME, downloadMaybeWrapperComp);
            additionalTrafoModels.add(uploadMaybeComp);
            additionalTrafoModels.add(downloadMaybeComp);

            // Assign source, target to wrapper component
            String sName = qCompSourceName.split("\\.")[qCompSourceName.split("\\.").length - 1];
            addSubComponentInstantiation(uploadMaybeWrapperComp, getPortFullyQName(connection.source, targetComp, modelPath), sName);
            String tName = qCompTargetName.split("\\.")[qCompTargetName.split("\\.").length - 1];
            addSubComponentInstantiation(downloadMaybeWrapperComp, getPortFullyQName(connection.target, targetComp, modelPath), tName);

            // Replace connections acc. to trafo
            this.replaceConnection(targetComp, connection.source, connection.target, uploadMaybeComp, downloadMaybeComp,
                                   uploadMaybeWrapperComp, downloadMaybeWrapperComp, allModels);

            // Generate behavior for up- and download
            this.generateUploadBehavior(uploadMaybeComp);
            this.generateDownloadBehavior(downloadMaybeComp);

            // Generate mtcfg to prevent splitting
            this.generateMtcfg(targetComp);

            // Remove source, target from target component
            removeSubcomponentInstantiation(targetComp, Collections.singletonList(sName));
            removeSubcomponentInstantiation(targetComp, Collections.singletonList(tName));

            // Set connection as transformed
            alreadyTransformed.add(qCompSourceName + "," + qCompTargetName);
          }
        }

      }
    }

    Log.info("Return " + additionalTrafoModels.size() + " additional trafo models", TOOL_NAME);
    
    return additionalTrafoModels;
  }

  private List<String> getQCompInstanceNames(ASTMACompilationUnit targetComp, List<ASTMACompilationUnit> allModels) {
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

    return qCompInstanceNames;
  }

  private List<FindConnectionsVisitor.Connection> getInConnections(ASTPortAccess portSource, List<FindConnectionsVisitor.Connection> connections) {
    List<FindConnectionsVisitor.Connection> inConnections = new ArrayList<>();

    for (FindConnectionsVisitor.Connection connection : connections) {
      if (connection.target.isPresentComponent() && connection.target.getQName().equals(portSource.getQName())) {
        inConnections.add(connection);
      }
    }

    return inConnections;
  }

  private List<FindConnectionsVisitor.Connection> getOutConnections(ASTPortAccess portTarget, List<FindConnectionsVisitor.Connection> connections) {
    List<FindConnectionsVisitor.Connection> outConnections = new ArrayList<>();

    for (FindConnectionsVisitor.Connection connection : connections) {
      if (connection.source.isPresentComponent() && connection.source.getQName().equals(portTarget.getQName())) {
        outConnections.add(connection);
      }
    }

    return outConnections;
  }

  private void replaceConnection(ASTMACompilationUnit comp, ASTPortAccess portSource, ASTPortAccess portTarget,
                                 ASTMACompilationUnit uploadMaybeComponent, ASTMACompilationUnit downloadMaybeComponent,
                                 ASTMACompilationUnit uploadMaybeWrapperComponent, ASTMACompilationUnit downloadMaybeWrapperComponent,
                                 List<ASTMACompilationUnit> allModels) throws Exception {
    // Add ports and connections to UploadMaybeWrapper
    List<FindConnectionsVisitor.Connection> inConnections = this.getInConnections(portSource, this.getConnections(comp));

    for (FindConnectionsVisitor.Connection connection : inConnections) {
      if (connection.source.isPresentComponent()) {
        ASTMCType portType = this.getPortType(connection.source, comp, allModels, this.modelPath);

        Log.info("Add new in-port " + INPORT_NAME + " of type " + portType + " to component " + UPLOAD_MAYBE_WRAPPER_NAME, TOOL_NAME);
        addPort(uploadMaybeWrapperComponent, INPORT_NAME, false, portType);

        // Source -> Wrapper
        Log.info("Add new connection " + connection.source.getQName() + "." + INPORT_NAME + " -> " + UPLOAD_MAYBE_WRAPPER_NAME.toLowerCase() +
                " to component " + comp.getComponentType().getName(), TOOL_NAME);
        addConnection(comp, connection.source.getQName() + "." + INPORT_NAME, UPLOAD_MAYBE_WRAPPER_NAME.toLowerCase());

        // Wrapper -> A
        Log.info("Add new connection " + UPLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + INPORT_NAME + " -> " + portSource.getQName() +
                " to component " + uploadMaybeWrapperComponent.getComponentType().getName(), TOOL_NAME);
        addConnection(uploadMaybeWrapperComponent, UPLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + INPORT_NAME, portSource.getQName());
      }
    }

    // Add ports and connections to DownloadMaybeWrapper
    List<FindConnectionsVisitor.Connection> outConnections = this.getOutConnections(portTarget, this.getConnections(comp));

    for (FindConnectionsVisitor.Connection connection : outConnections) {
      if (connection.target.isPresentComponent()) {
        ASTMCType portType = this.getPortType(portTarget, comp, allModels, this.modelPath);

        Log.info("Add new out-port " + OUTPORT_NAME + " of type " + portType + " to component " + DOWNLOAD_MAYBE_WRAPPER_NAME, TOOL_NAME);
        addPort(downloadMaybeWrapperComponent, OUTPORT_NAME, true, portType);

        // B -> Wrapper
        Log.info("Add new connection " + portTarget.getQName() + "." + OUTPORT_NAME + " -> " + DOWNLOAD_MAYBE_WRAPPER_NAME.toLowerCase() +
                " to component " + comp.getComponentType().getName(), TOOL_NAME);
        addConnection(comp, portTarget.getQName() + "." + OUTPORT_NAME, DOWNLOAD_MAYBE_WRAPPER_NAME.toLowerCase());

        // Wrapper -> Target
        Log.info("Add new connection " + DOWNLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + OUTPORT_NAME + " -> " + connection.target.getQName() +
                " to component " + downloadMaybeWrapperComponent.getComponentType().getName(), TOOL_NAME);
        addConnection(downloadMaybeWrapperComponent, DOWNLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + OUTPORT_NAME, connection.target.getQName());
      }
    }

    // Add ports for UploadMaybe, DownloadMaybe, UploadMaybeWrapper, DownloadMaybeWrapper
    ASTMCType portType = this.getPortType(portSource, comp, allModels, this.modelPath);

    Log.info("Add new in-port " + INPORT_NAME + " of type " + portType + " to component " + UPLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(uploadMaybeComponent, INPORT_NAME, false, portType);

    Log.info("Add new out-port " + PORT_URL_NAME + " of type " + this.getUrlPortType() + " to component " + UPLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(uploadMaybeComponent, PORT_URL_NAME, true, this.getUrlPortType());

    Log.info("Add new out-port " + PORT_DATA_NAME + " of type " + portType + " to component " + UPLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(uploadMaybeComponent, PORT_DATA_NAME, true, portType);

    Log.info("Add new out-port " + PORT_URL_NAME + " of type " + this.getUrlPortType() + " to component " + UPLOAD_MAYBE_WRAPPER_NAME, TOOL_NAME);
    addPort(uploadMaybeWrapperComponent, PORT_URL_NAME, true, this.getUrlPortType());

    Log.info("Add new out-port " + PORT_DATA_NAME + " of type " + portType + " to component " + UPLOAD_MAYBE_WRAPPER_NAME, TOOL_NAME);
    addPort(uploadMaybeWrapperComponent, PORT_DATA_NAME, true, portType);

    Log.info("Add new in-port " + PORT_URL_NAME + " of type " + this.getUrlPortType() + " to component " + DOWNLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(downloadMaybeComponent, PORT_URL_NAME, false, this.getUrlPortType());

    Log.info("Add new in-port " + PORT_DATA_NAME + " of type " + portType + " to component " + DOWNLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(downloadMaybeComponent, PORT_DATA_NAME, false, portType);

    Log.info("Add new in-port " + PORT_URL_NAME + " of type " + this.getUrlPortType() + " to component " + DOWNLOAD_MAYBE_WRAPPER_NAME, TOOL_NAME);
    addPort(downloadMaybeWrapperComponent, PORT_URL_NAME, false, this.getUrlPortType());

    Log.info("Add new in-port " + PORT_DATA_NAME + " of type " + portType + " to component " + DOWNLOAD_MAYBE_WRAPPER_NAME, TOOL_NAME);
    addPort(downloadMaybeWrapperComponent, PORT_DATA_NAME, false, portType);

    Log.info("Add new out-port " + OUTPORT_NAME + " of type " + portType + " to component " + DOWNLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(downloadMaybeComponent, OUTPORT_NAME, true, portType);

    // A -> UploadMaybe
    Log.info("Add new connection " + portSource.getQName() + " -> " + UPLOAD_MAYBE_NAME.toLowerCase() + "." + INPORT_NAME +
            " to component " + uploadMaybeWrapperComponent.getComponentType().getName(), TOOL_NAME);
    addConnection(uploadMaybeWrapperComponent, portSource.getQName(), UPLOAD_MAYBE_NAME.toLowerCase() + "." + INPORT_NAME);

    // DownloadMaybe -> B
    Log.info("Add new connection " + DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + OUTPORT_NAME + " -> " + portTarget.getQName() +
            " to component " + downloadMaybeWrapperComponent.getComponentType().getName(), TOOL_NAME);
    addConnection(downloadMaybeWrapperComponent, DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + OUTPORT_NAME, portTarget.getQName());

    // UploadMaybe -> UploadMaybeWrapper
    Log.info("Add new connection " + UPLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_URL_NAME  + " -> " + PORT_URL_NAME +
            " to component " + uploadMaybeWrapperComponent.getComponentType().getName(), TOOL_NAME);
    addConnection(uploadMaybeWrapperComponent, UPLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_URL_NAME, PORT_URL_NAME);

    // UploadMaybe -> UploadMaybeWrapper
    Log.info("Add new connection " + UPLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_DATA_NAME + " -> " + PORT_DATA_NAME +
            " to component " + uploadMaybeWrapperComponent.getComponentType().getName(), TOOL_NAME);
    addConnection(uploadMaybeWrapperComponent, UPLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_DATA_NAME, PORT_DATA_NAME);

    // DownloadMaybeWrapper -> DownloadMaybe
    Log.info("Add new connection " + PORT_URL_NAME  + " -> " + DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_URL_NAME +
            " to component " + downloadMaybeWrapperComponent.getComponentType().getName(), TOOL_NAME);
    addConnection(downloadMaybeWrapperComponent, PORT_URL_NAME, DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_URL_NAME);

    // DownloadMaybeWrapper -> DownloadMaybe
    Log.info("Add new connection " + PORT_DATA_NAME + " -> " + DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_DATA_NAME +
            " to component " + downloadMaybeWrapperComponent.getComponentType().getName(), TOOL_NAME);
    addConnection(downloadMaybeWrapperComponent, PORT_DATA_NAME, DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_DATA_NAME);

    // UploadMaybeWrapper -> DownloadMaybeWrapper
    Log.info("Add new connection " + UPLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + PORT_URL_NAME + " -> " + DOWNLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + PORT_URL_NAME +
            " to component " + comp.getComponentType().getName(), TOOL_NAME);
    addConnection(comp, UPLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + PORT_URL_NAME, DOWNLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + PORT_URL_NAME);

    // UploadMaybeWrapper -> DownloadMaybeWrapper
    Log.info("Add new connection " + UPLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + PORT_DATA_NAME + " -> " + DOWNLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + PORT_DATA_NAME +
            " to component " + comp.getComponentType().getName(), TOOL_NAME);
    addConnection(comp, UPLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + PORT_DATA_NAME, DOWNLOAD_MAYBE_WRAPPER_NAME.toLowerCase() + "." + PORT_DATA_NAME);

    Log.info("Remove existing connection " + portSource.getQName() + " -> " + portTarget.getQName() +
            " from component " + comp.getComponentType(), TOOL_NAME);
    removeConnection(comp, portSource, portTarget);

  }

  private ASTMCType getUrlPortType() {
    ASTMCQualifiedName qualifiedName = MontiThingsMill.mCQualifiedNameBuilder().addParts(PORT_URL_TYPE).build();
    return MontiThingsMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualifiedName).build();
  }

  private void generateUploadBehavior(ASTMACompilationUnit comp) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    this.generate(tHwcPath, UPLOAD_MAYBE_NAME + "Impl", ".cpp", UPLOAD_MAYBE_IMPL_CPP,
            comp.getPackage().getQName(), UPLOAD_MAYBE_NAME, MAX_PORT_SIZE, CONTAINERNAME, PORT_URL_NAME, PORT_DATA_NAME, INPORT_NAME);

    this.generate(sHwcPath, UPLOAD_MAYBE_NAME + "Impl", ".cpp", UPLOAD_MAYBE_IMPL_CPP,
            comp.getPackage().getQName(), UPLOAD_MAYBE_NAME, MAX_PORT_SIZE, CONTAINERNAME, PORT_URL_NAME, PORT_DATA_NAME, INPORT_NAME);

    this.generate(tHwcPath, UPLOAD_MAYBE_NAME + "Impl", ".h", UPLOAD_MAYBE_IMPL_HEADER,
            comp.getPackage().getQName(), UPLOAD_MAYBE_NAME);

    this.generate(sHwcPath, UPLOAD_MAYBE_NAME + "Impl", ".h", UPLOAD_MAYBE_IMPL_HEADER,
            comp.getPackage().getQName(), UPLOAD_MAYBE_NAME);
  }

  private void generateDownloadBehavior(ASTMACompilationUnit comp) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    this.generate(tHwcPath, DOWNLOAD_MAYBE_NAME + "Impl", ".cpp", DOWNLOAD_MAYBE_IMPL_CPP,
            comp.getPackage().getQName(), DOWNLOAD_MAYBE_NAME, PORT_URL_NAME, PORT_DATA_NAME, OUTPORT_NAME);

    this.generate(sHwcPath, DOWNLOAD_MAYBE_NAME + "Impl", ".cpp", DOWNLOAD_MAYBE_IMPL_CPP,
            comp.getPackage().getQName(), DOWNLOAD_MAYBE_NAME, PORT_URL_NAME, PORT_DATA_NAME, OUTPORT_NAME);

    this.generate(tHwcPath, DOWNLOAD_MAYBE_NAME + "Impl", ".h", DOWNLOAD_MAYBE_IMPL_HEADER,
            comp.getPackage().getQName(), DOWNLOAD_MAYBE_NAME);

    this.generate(sHwcPath, DOWNLOAD_MAYBE_NAME + "Impl", ".h", DOWNLOAD_MAYBE_IMPL_HEADER,
            comp.getPackage().getQName(), DOWNLOAD_MAYBE_NAME);
  }

  private void generateMtcfg(ASTMACompilationUnit comp) {
    File tHwcPath = Paths.get(this.targetHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();
    File sHwcPath = Paths.get(this.srcHwcPath.getAbsolutePath(), comp.getPackage().getQName()).toFile();

    this.generate(tHwcPath, UPLOAD_MAYBE_WRAPPER_NAME, ".mtcfg", UPLOAD_MAYBE_WRAPPER_MTCFG,
            comp.getPackage().getQName(), UPLOAD_MAYBE_WRAPPER_NAME);

    this.generate(sHwcPath, UPLOAD_MAYBE_WRAPPER_NAME, ".mtcfg", UPLOAD_MAYBE_WRAPPER_MTCFG,
            comp.getPackage().getQName(), UPLOAD_MAYBE_WRAPPER_NAME);

    this.generate(tHwcPath, DOWNLOAD_MAYBE_WRAPPER_NAME, ".mtcfg", DOWNLOAD_MAYBE_WRAPPER_MTCFG,
            comp.getPackage().getQName(), DOWNLOAD_MAYBE_WRAPPER_NAME);

    this.generate(sHwcPath, DOWNLOAD_MAYBE_WRAPPER_NAME, ".mtcfg", DOWNLOAD_MAYBE_WRAPPER_MTCFG,
            comp.getPackage().getQName(), DOWNLOAD_MAYBE_WRAPPER_NAME);
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
