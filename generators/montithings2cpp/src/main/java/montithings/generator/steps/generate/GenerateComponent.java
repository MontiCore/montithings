// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import behavior._ast.ASTConnectStatement;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._visitor.MontiThingsTraverser;
import montithings.generator.codegen.MTGenerator;
import montithings.generator.config.ConfigParams;
import montithings.generator.config.MessageBroker;
import montithings.generator.config.SplittingMode;
import montithings.generator.config.TargetPlatform;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.GeneratorHelper;
import montithings.generator.steps.GeneratorStep;
import montithings.generator.steps.helper.GenerateCDEAdapter;
import montithings.generator.visitor.FindConnectStatementsVisitor;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;
import static montithings.generator.helper.FileHelper.copyHwcToTarget;
import static montithings.generator.helper.FileHelper.getSubPackagesPath;

public class GenerateComponent extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    for (Map.Entry<ComponentTypeSymbol, Set<ComponentTypeSymbol>> e : state.getModelPacks()
      .entrySet()) {
      String baseModel = e.getKey().getFullName();
      Set<ComponentTypeSymbol> enclosingModels = e.getValue();

      File compTarget = state.getTarget();

      if (state.getConfig().getSplittingMode() != SplittingMode.OFF) {
        compTarget = Paths.get(state.getTarget().getAbsolutePath(), baseModel).toFile();
        state.setMtg(new MTGenerator(compTarget, state.getHwcPath(), state.getConfig()));

        if (state.getConfig().getSplittingMode() == SplittingMode.LOCAL) {
          ComponentTypeSymbol comp = state.getTool().modelToSymbol(baseModel, state.getSymTab());
          state.getMtg().generatePortJson(compTarget, comp);
        }

        GenerateCDEAdapter.generateCDEAdapter(compTarget, state);
      }
      if (state.getConfig().getMessageBroker() == MessageBroker.DDS) {
        state.getMtg().generateDDSDCPSConfig(compTarget);
      }

      Set<ComponentTypeSymbol> dynConnectedSubcomps = getDynamicallyConnectedSubcomps(e.getKey());

      // Save splitting mode and message broker for overriding it for subcomponents that should be included in the same binary.
      SplittingMode orgSplit = state.getConfig().getSplittingMode();
      MessageBroker orgBroker = state.getConfig().getMessageBroker();

      for (ComponentTypeSymbol symModel : enclosingModels) {
        String model = symModel.getFullName();
        boolean genDeploy = model.equals(baseModel);

        // Only the deployed component should communicate directly with the 'outer world'.
        // All the other enclosed components should communicate using native ports.
        // Unless its dynamically connected. Then it needs to communicate.
        state.getConfig().setSplittingMode(genDeploy ? orgSplit : SplittingMode.OFF);
        if (!dynConnectedSubcomps.contains(symModel)) {
          state.getConfig()
            .setMessageBroker(genDeploy ? orgBroker : MessageBroker.OFF);
        }

        generateCppForComponent(model, compTarget, state, genDeploy);

        if (!genDeploy) {
          // copy hwc for embedded component manually
          copyHwcToTarget(new File(state.getTarget(), baseModel), state.getHwcPath(), model,
            state.getConfig(), state.getModels());
        }
      }
      // reset splitting mode and message broker
      state.getConfig().setSplittingMode(orgSplit);
      state.getConfig().setMessageBroker(orgBroker);

      generateCMakeForComponent(baseModel, compTarget, state);

      state.setMtg(new MTGenerator(state.getTarget(), state.getHwcPath(), state.getConfig()));
    }
  }

  protected void generateCppForComponent(String model, File target, GeneratorToolState state,
    boolean generateDeploy) {
    ComponentTypeSymbol comp = state.getTool().modelToSymbol(model, state.getSymTab());
    Log.info("Generate MT model: " + comp.getFullName(), TOOL_NAME);

    // check if component is implementation
    if (comp.getAstNode() instanceof ASTMTComponentType &&
      ((ASTMTComponentType) comp.getAstNode()).getMTComponentModifier().isInterface()) {
      // Dont generate files for implementation. They are generated when interface is there
      return;
    }

    String compname = comp.getName();

    // Check if component is interface
    Optional<ComponentTypeSymbol> implementation = state.getConfig().getBinding(comp);
    if (implementation.isPresent()) {
      compname = implementation.get().getName();
    }

    // Generate Files
    state.getMtg().generateAll(Paths.get(target.getAbsolutePath(),
        Names.getPathFromPackage(comp.getPackageName())).toFile(),
      comp, generateDeploy);

    generateHwcPort(target, state.getConfig(), comp);

    if (state.getConfig().getSplittingMode() != SplittingMode.OFF) {
      copyHwcToTarget(target, state.getHwcPath(), model, state.getConfig(),
        state.getModels());
    }
  }

  protected void generateCMakeForComponent(String model, File target, GeneratorToolState state) {
    ComponentTypeSymbol comp = state.getTool().modelToSymbol(model, state.getSymTab());

    if (ComponentHelper.isApplication(comp, state.getConfig())
      || state.getConfig().getSplittingMode() != SplittingMode.OFF) {
      File libraryPath = Paths.get(target.getAbsolutePath(), "montithings-RTE").toFile();
      // Check for Subpackages
      File[] subPackagesPath = getSubPackagesPath(state.getModelPath().getAbsolutePath());

      // generate cmake file
      if (state.getConfig().getTargetPlatform()
        != TargetPlatform.ARDUINO) { // Arduino uses its own build system
        Log.info("Generate CMake file for " + comp.getFullName(), "MontiThingsGeneratorTool");
        state.getMtg().generateMakeFile(target, comp, libraryPath, subPackagesPath,
          state.getExecutableSensorActuatorPorts());
        if (state.getConfig().getSplittingMode() != SplittingMode.OFF) {
          state.getMtg()
            .generateScripts(target, comp, state.getExecutableSensorActuatorPorts(),
              state.getHwcPythonScripts(),
              state.getExecutableSubdirs());
        }
      }
    }
  }

  /**
   * Initializes generation for a C++ port,
   * if appropriate C++ code templates are provided,
   *
   * @param target target directory for all artifacts.
   * @param config Generator configuration
   * @param comp   Component containing the ports for which C++ code should be generated.
   */
  protected void generateHwcPort(File target, ConfigParams config, ComponentTypeSymbol comp) {
    for (PortSymbol port : comp.getPorts()) {
      if (config.getTemplatedPorts().contains(port)) {
        Optional<String> portType = GeneratorHelper.getPortHwcTemplateName(port, config);
        portType.ifPresent(s ->
          MTGenerator.generateAdditionalPort(config.getHwcTemplatePath(), target, s, config, port));
      }
    }
  }

  protected Set<ComponentTypeSymbol> getDynamicallyConnectedSubcomps(
    ComponentTypeSymbol enclosingComp) {
    Set<ComponentTypeSymbol> result = new HashSet<>();

    // Find all connect statements
    FindConnectStatementsVisitor visitor = new FindConnectStatementsVisitor();
    Set<ASTBehavior> behaviors = enclosingComp.getAstNode().getBody().getArcElementList().stream()
      .filter(e -> e instanceof ASTBehavior)
      .map(e -> (ASTBehavior) e)
      .collect(Collectors.toSet());
    MontiThingsTraverser traverser = visitor.createTraverser();
    for (ASTBehavior b : behaviors) {
      b.accept(traverser);
    }

    // Get the types of all component instances accessed in connect statements
    for (ASTConnectStatement cs : visitor.getConnectStatements()) {
      Set<ASTPortAccess> portAccesses = new HashSet<>();
      portAccesses.add(cs.getConnector().getSource());
      portAccesses.addAll(cs.getConnector().getTargetList());

      for (ASTPortAccess pa : portAccesses) {
        if (pa.isPresentComponentSymbol()) {
          result.add(pa.getComponentSymbol().getType());
        }
      }
    }

    return result;
  }

}
