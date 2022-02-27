// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps;

import montithings.generator.data.GeneratorToolState;
import montithings.generator.data.Models;

/**
 * Finds models in the model path
 */
public class FindModels extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    state.setModels(new Models(state.getModelPath()));
  }

}

