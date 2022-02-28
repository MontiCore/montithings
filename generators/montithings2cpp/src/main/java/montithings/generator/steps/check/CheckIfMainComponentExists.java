// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.check;

import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import montithings.util.MontiThingsError;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Checks if the main component set by the user actually exists
 */
public class CheckIfMainComponentExists extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    List<String> allComponentsList = state.getModels().getMontithings().stream()
      .map(c -> Names.getQualifier(c) + (Names.getQualifier(c).isEmpty() ? "" : ".")
        + Names.getSimpleName(c))
      .collect(Collectors.toList());

    String allComponents = String.join(", ", allComponentsList);

    if (!state.getSymTab().resolveComponentType(state.getConfig().getMainComponent()).isPresent()) {
      Log.error(String.format(MontiThingsError.GENERATOR_MAIN_UNKNOWN.toString(),
        state.getConfig().getMainComponent(), allComponents)
      );
    }

  }

}
