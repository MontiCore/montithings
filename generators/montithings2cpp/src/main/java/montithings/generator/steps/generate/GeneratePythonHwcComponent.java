package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.Names;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GeneratePythonHwcComponent extends GeneratorStep {
  @Override
  public void action(GeneratorToolState state) {
    List<String> allComponentsList = state.getModels().getMontithings().stream()
        .map(c -> Names.getQualifier(c) + (Names.getQualifier(c).isEmpty() ? "" : ".")
            + Names.getSimpleName(c))
        .collect(Collectors.toList());

    List<String> protobufPythonOutModules = state.getProtoFiles().stream()
        .map(Path::toFile)
        .map(File::getName)
        .map(s -> s.replace(".proto", "_pb2"))
        .collect(Collectors.toList());

    for (String compName : allComponentsList) {
      ComponentTypeSymbol comp = state.getTool().modelToSymbol(compName, state.getSymTab());
      File implCandidate = Paths.get(state.getHwcPath().toString(), compName.replace('.', File.separatorChar) + "Impl.py").toFile();
      if (implCandidate.exists() && implCandidate.isFile()) {
        String[] res = compName.split("\\.");
        String simpleName = res[res.length - 1];

        // TODO: Fill with real values
        ArrayList<PortSymbol> InPorts = (ArrayList<PortSymbol>) comp.getIncomingPorts();
        ArrayList<PortSymbol> OutPorts = (ArrayList<PortSymbol>) comp.getOutgoingPorts();

        GeneratorSetup setup = new GeneratorSetup();
        setup.setCommentStart("\"\"\"");
        setup.setCommentEnd("\"\"\"");
        setup.setOutputDirectory(Paths.get(state.getTarget().toString(), "hwc").toFile());
        GeneratorEngine ge = new GeneratorEngine(setup);

        Path targetPath = Paths.get(state.getTarget().toString(), "hwc");
        for(String name: compName.split("\\.")) {
          targetPath = Paths.get(name);
        }

        Path topComponentPath = Paths.get(targetPath + "ImplTOP.py");
        ge.generateNoA("template.util.pythonComponent.ComponentTOP.ftl",
            topComponentPath,
            simpleName,
            protobufPythonOutModules.get(0),
            InPorts,
            OutPorts);
        Path execPath = Paths.get(targetPath + ".py");
        ge.generateNoA("template.util.pythonComponent.Component.ftl",
            execPath,
            simpleName);
      }
    }
  }
}
