// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbolTOP;
import montithings.generator.config.ReplayMode;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * clear list of templated ports if replay mode is on since they get mocked by a trafo
 */
public class ClearTemplatesIfReplay extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    if (state.getConfig().getReplayMode() == ReplayMode.ON) {
      state.getConfig().getTemplatedPorts().clear();

      List<String> allModels = state.getSymTab().getSubScopes().stream()
        .map(s -> s.getComponentTypeSymbols().values())
        .flatMap(Collection::stream)
        .map(ComponentTypeSymbolTOP::getFullName)
        .collect(Collectors.toList());
      state.getModels().setMontithings(allModels);
    }
  }

}
