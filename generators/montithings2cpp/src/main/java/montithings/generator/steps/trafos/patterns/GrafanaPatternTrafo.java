package montithings.generator.steps.trafos.patterns;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTMACompilationUnit;
import montithings.MontiThingsMill;
import montithings._visitor.FindConnectionsVisitor;
import montithings.generator.data.GeneratorToolState;
import montithings.trafos.BasicTransformations;
import montithings.trafos.MontiThingsTrafo;
import montithings.util.TrafoUtil;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GrafanaPatternTrafo extends BasicTransformations implements MontiThingsTrafo {
  private static final String TOOL_NAME = "GrafanaPatternTrafo";
  private static final String INJECTOR_NAME = "UploadMaybe";
  private final File modelPath;
  private final File targetHwcPath;
  private final File srcHwcPath;
  private final GeneratorToolState state;
  private ASTMACompilationUnit injectorComp;

  public GrafanaPatternTrafo(GeneratorToolState state) {
    this.modelPath = state.getModelPath();
    this.srcHwcPath = state.getHwcPath();
    this.targetHwcPath = Paths.get(state.getTarget().getAbsolutePath(), "hwc").toFile();
    this.state = state;
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
            ASTMCType portType = this.getPortType(connection.target, targetComp, allModels, this.modelPath);

            //  Generate Injector Component Type (only once like in Anomaly Detection)
            if (injectorComp == null) {
              injectorComp = this.getInterceptComponent(INJECTOR_NAME, targetComp);
              additionalTrafoModels.add(injectorComp);
              allModels.add(injectorComp);
            }

            //  Add port to targetComp of Injector Component Type for this connection
            String inPortName = "connect" + "Co" + injectorComp.getComponentType().getName();
            String outPortName = "disconnect" + "Co" + injectorComp.getComponentType().getName();
            addPort(targetComp, inPortName, false, getInjectorPortType(injectorComp));
            addPort(targetComp, outPortName, true, getInjectorPortType(injectorComp));

            // TODO: Add behavior block for the new port

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
}
