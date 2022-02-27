// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import arcbasis._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;
import montithings.generator.config.SplittingMode;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.steps.GeneratorStep;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static montithings.generator.MontiThingsGeneratorTool.TOOL_NAME;

public class FindModelPacks extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    // determine the packs of components for each (base) model
    Map<ComponentTypeSymbol, Set<ComponentTypeSymbol>> modelPacks = new HashMap<>();

    for (String model : state.getModels().getMontithings()) {
      ComponentTypeSymbol comp = state.getTool().modelToSymbol(model, state.getSymTab());

      // If this component does not need its own executable, then we can just
      // ignore it right here. If splitting is turned of, we will generate
      // everything due to compatibility reasons.
      if (state.getExecutableComponents().contains(comp)
        || state.getConfig().getSplittingMode() == SplittingMode.OFF) {
        // aggregate all of the components that should be packed with this
        // component
        Set<ComponentTypeSymbol> includeModels = new HashSet<>();
        modelPacks.put(comp, includeModels);

        // the component itself should obviously be part of the deployment
        includeModels.add(comp);

        // all (in-)direct sub-components should be part of the deployment if
        // component should be deployed with its subcomponents
        if (ComponentHelper.shouldIncludeSubcomponents(comp, state.getConfig())) {
          for (ComponentTypeSymbol sub : ComponentHelper.getSubcompTypesRecursive(comp)) {
            Log.debug("Including model \"" + sub.getFullName() + "\" with deployment of \""
              + comp.getFullName() + "\"", TOOL_NAME);
            includeModels.add(sub);
          }
        }
      }
    }

    state.setModelPacks(modelPacks);
  }
}
