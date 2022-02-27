// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.steps.GeneratorStep;

public class GenerateBuildScripts extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    for (String model : state.getModels().getMontithings()) {
      ComponentTypeSymbol comp = state.getTool().modelToSymbol(model, state.getSymTab());
      if (ComponentHelper.isApplication(comp, state.getConfig())) {
        state.getMtg().generateBuildScript(state.getTarget(), comp, state.getHwcPythonScripts());
        state.getMtg().generateDockerfileScript(state.getTarget(), comp,
          state.getExecutableSensorActuatorPorts(), state.getHwcPythonScripts());
        state.getMtg().generateCrosscompileScript(state.getTarget(), comp);
      }
    }
  }

}
