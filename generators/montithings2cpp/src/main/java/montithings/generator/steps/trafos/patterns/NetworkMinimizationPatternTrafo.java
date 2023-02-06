package montithings.generator.steps.trafos.patterns;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTPortAccess;
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
import java.util.*;

public class NetworkMinimizationPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
  private static final String TOOL_NAME = "NetworkMinimizationPatternTrafo";
  private static final String UPLOAD_MAYBE_NAME = "UploadMaybe";
  private static final String DOWNLOAD_MAYBE_NAME = "DownloadMaybe";
  private static final String INPORT_NAME = "in";
  private static final String OUTPORT_NAME = "out";
  private static final String PORT_URL_NAME = "url";
  private static final String PORT_URL_TYPE = "String";
  private static final String PORT_DATA_NAME = "orig";
  private final File modelPath;

  public NetworkMinimizationPatternTrafo(GeneratorToolState state) {
    this.modelPath = state.getModelPath();
  }

  @Override
  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels,
                                                    Collection<ASTMACompilationUnit> addedModels,
                                                    ASTMACompilationUnit targetComp) throws Exception {
    Log.info("Apply transformation to: " + targetComp.getComponentType().getName(), TOOL_NAME);

    Collection<ASTMACompilationUnit> additionalTrafoModels = new ArrayList<>();

    List<ASTMACompilationUnit> allModels = this.getAllModels(originalModels, addedModels);

    Log.info("Return " + additionalTrafoModels.size() + " additional trafo models", TOOL_NAME);

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
            // Generate interceptor components for up- and download
            ASTMACompilationUnit uploadMaybeComp = this.getInterceptComponent(UPLOAD_MAYBE_NAME, targetComp);
            ASTMACompilationUnit downloadMaybeComp = this.getInterceptComponent(DOWNLOAD_MAYBE_NAME, targetComp);
            additionalTrafoModels.add(uploadMaybeComp);
            additionalTrafoModels.add(downloadMaybeComp);

            // Replace connections acc. to trafo
            this.replaceConnection(targetComp, connection.source, connection.target,
                    this.getPortType(connection.source, targetComp, allModels, this.modelPath),
                    uploadMaybeComp, downloadMaybeComp);

            // Generate behavior for up- and download
            this.generateUploadBehavior();
            this.generateDownloadBehavior();

            // Generate mtcfg to prevent splitting
            this.generateMtcfg();

            // Set connection as transformed
            alreadyTransformed.add(qCompSourceName + "," + qCompTargetName);
          }
        }

      }
    }

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

  private void replaceConnection(ASTMACompilationUnit comp, ASTPortAccess portSource, ASTPortAccess portTarget, ASTMCType portType,
                                 ASTMACompilationUnit uploadMaybeComponent, ASTMACompilationUnit downloadMaybeComponent) {
    // Add ports for UploadMaybe, DownloadMaybe
    Log.info("Add new in-port " + INPORT_NAME + " of type " + portType + " to component " + UPLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(uploadMaybeComponent, INPORT_NAME, false, portType);

    Log.info("Add new out-port " + PORT_URL_NAME + " of type " + this.getUrlPortType() + " to component " + UPLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(uploadMaybeComponent, PORT_URL_NAME, true, portType);

    Log.info("Add new out-port " + PORT_DATA_NAME + " of type " + portType + " to component " + UPLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(uploadMaybeComponent, PORT_DATA_NAME, true, portType);

    Log.info("Add new in-port " + PORT_URL_NAME + " of type " + this.getUrlPortType() + " to component " + DOWNLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(downloadMaybeComponent, PORT_URL_NAME, false, portType);

    Log.info("Add new in-port " + PORT_DATA_NAME + " of type " + portType + " to component " + DOWNLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(downloadMaybeComponent, PORT_DATA_NAME, false, portType);

    Log.info("Add new out-port " + OUTPORT_NAME + " of type " + portType + " to component " + DOWNLOAD_MAYBE_NAME, TOOL_NAME);
    addPort(downloadMaybeComponent, OUTPORT_NAME, true, portType);

    // Add connections from A -> UploadMaybe, UploadMaybe -> DownloadMaybe, DownloadMaybe -> B
    Log.info("Add new connection " + portSource.getQName() + " -> " + UPLOAD_MAYBE_NAME.toLowerCase() + "." + INPORT_NAME +
            " to component " + comp.getComponentType(), TOOL_NAME);
    addConnection(comp, portSource.getQName(), UPLOAD_MAYBE_NAME.toLowerCase() + "." + INPORT_NAME);

    Log.info("Add new connection " + DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + OUTPORT_NAME + " -> " + portTarget.getQName() +
            " to component " + comp.getComponentType(), TOOL_NAME);
    addConnection(comp, DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + OUTPORT_NAME, portTarget.getQName());

    Log.info("Add new connection " + UPLOAD_MAYBE_NAME.toLowerCase() + " -> " + DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_URL_NAME +
            " to component " + comp.getComponentType(), TOOL_NAME);
    addConnection(comp, UPLOAD_MAYBE_NAME.toLowerCase(), DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_URL_NAME);

    Log.info("Add new connection " + UPLOAD_MAYBE_NAME.toLowerCase() + " -> " + DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_DATA_NAME +
            " to component " + comp.getComponentType(), TOOL_NAME);
    addConnection(comp, UPLOAD_MAYBE_NAME.toLowerCase(), DOWNLOAD_MAYBE_NAME.toLowerCase() + "." + PORT_DATA_NAME);

    // Remove connection from A -> B
    Log.info("Remove existing connection " + portSource.getQName() + " -> " + portTarget.getQName() +
            " from component " + comp.getComponentType(), TOOL_NAME);
    removeConnection(comp, portSource, portTarget);
  }

  private ASTMCType getUrlPortType() {
    ASTMCQualifiedName qualifiedName = MontiThingsMill.mCQualifiedNameBuilder().addParts(PORT_URL_TYPE).build();
    return MontiThingsMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualifiedName).build();
  }

  private void generateUploadBehavior() {
    // Todo:
    //   Create ftl and generate here
  }

  private void generateDownloadBehavior() {
    // Todo:
    //   Create ftl and generate here
  }

  private void generateMtcfg() {
    // Todo:
    //  Create ftl and generate here
    //  Set to AST in CheckMtConfig
  }
}
