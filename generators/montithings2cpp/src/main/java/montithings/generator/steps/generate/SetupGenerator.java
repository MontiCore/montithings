// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.generate;

import montithings.generator.codegen.MTGenerator;
import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;

public class SetupGenerator extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    state.setMtg(new MTGenerator(state.getTarget(), state.getHwcPath(), state.getConfig()));
  }

}
