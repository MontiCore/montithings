package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import montithings.generator.codegen.FileGenerator;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneratePythonPorts extends GeneratorStep {
  @Override
  public void action(GeneratorToolState state) {
    // Identify components with behaviour implemented in Python
    Set<ComponentTypeSymbol> components = findPythonComponents(state);

    File targetPath = state.getTarget();
    FileGenerator fg = new FileGenerator(targetPath, state.getConfig().getHwcPath());

    List<String> protobufPythonOutModules = state.getProtoFiles().stream()
        .map(Path::toFile)
        .map(File::getName)
        .map(s -> s.replace(".proto", "_pb2"))
        .collect(Collectors.toList());

    for (ComponentTypeSymbol comp: components) {
      String topFile = comp.getName() + "ImplTOP";
      String componentName = comp.getName();
      String templateBaseDir = "template/util/pythonComponent/";

      String subscribeTo = "";
      String publishTo = "";

      fg.generate(targetPath, topFile, ".py", templateBaseDir + "ComponentTOP.ftl", componentName);
      fg.generate(targetPath, componentName, ".py", templateBaseDir + "Component.ftl",
          protobufPythonOutModules.get(0),
          componentName,
          publishTo,
          subscribeTo);
    }
  }

  private Set<ComponentTypeSymbol> findPythonComponents(GeneratorToolState state) {
    Set<ComponentTypeSymbol> components = new HashSet<>();
    final Set<PortSymbol> ports = state.getConfig().getTemplatedPorts();
    for(PortSymbol p: ports) {
      if (!p.getComponent().isPresent()) {
        break;
      }
      final String componentName = p.getComponent().get().getFullName();
      final String componentPath = componentName.replace('.', File.separatorChar);
      final String implFilename = componentPath + "Impl.py";
      final Path implFilePath = Paths.get(state.getConfig().getHwcPath().toString(), implFilename);
      if (implFilePath.toFile().exists()) {
        final ComponentTypeSymbol component = p.getComponent().get();
        components.add(component);
      }
    }

    return components;
  }
}
