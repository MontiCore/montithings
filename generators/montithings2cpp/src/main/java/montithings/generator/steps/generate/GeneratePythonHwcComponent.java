package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.steps.GeneratorStep;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Generate the Python component from handwritten Python behaviours.
 */
public class GeneratePythonHwcComponent extends GeneratorStep {
  @Override
  public void action(GeneratorToolState state) {
    GeneratorEngine ge = getGeneratorEngine(state);

    List<ComponentTypeSymbol> components = state.getModels().getMontithings().stream()
        .map(s -> state.getTool().modelToSymbol(s, state.getSymTab()))
        .collect(Collectors.toList());

    for (ComponentTypeSymbol comp : components) {
      String unqualifiedName = comp.getName();
      if (ComponentHelper.hasHandwrittenPythonBehaviour(state.getHwcPath(), comp)) {
        ArrayList<PortSymbol> InPorts = (ArrayList<PortSymbol>) comp.getIncomingPorts();
        ArrayList<PortSymbol> OutPorts = (ArrayList<PortSymbol>) comp.getOutgoingPorts();
        List<String> protobufPythonOutModules = state.getProtoFiles().stream()
            .map(Path::getFileName)
            .map(Path::toString)
            .map(s -> s.replace(".proto", "_pb2"))
            .collect(Collectors.toList());
        ge.generateNoA("template.util.pythonComponent.ComponentTOP.ftl",
            Paths.get(unqualifiedName + "ImplTOP.py"),
            unqualifiedName,
            protobufPythonOutModules.get(0),
            InPorts,
            OutPorts);
        Path execPath = Paths.get(unqualifiedName + ".py");
        ge.generateNoA("template.util.pythonComponent.Component.ftl",
            execPath,
            unqualifiedName);
      }
    }
  }

  private static GeneratorEngine getGeneratorEngine(GeneratorToolState state) {
    GeneratorSetup setup = new GeneratorSetup();
    setup.setCommentStart("\"\"\"");
    setup.setCommentEnd("\"\"\"");
    setup.setOutputDirectory(Paths.get(state.getTarget().toString(), "hwc").toFile());
    return new GeneratorEngine(setup);
  }
}
