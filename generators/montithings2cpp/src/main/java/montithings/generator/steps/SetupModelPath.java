// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps;

import de.monticore.io.paths.ModelPath;
import montithings.generator.data.GeneratorToolState;

public class SetupModelPath extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    state.setMcModelPath(new ModelPath(state.getModelPath().toPath()));
  }

}
