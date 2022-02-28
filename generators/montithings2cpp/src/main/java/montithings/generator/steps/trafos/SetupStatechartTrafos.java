// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps.trafos;

import montithings.generator.data.GeneratorToolState;
import montithings.generator.steps.GeneratorStep;
import montithings.trafos.SimplifyStatechartTrafo;

/**
 * Adds Trafos for allowing simplified statechart transitions that allow multiple states as source,
 * i.e. "A, B -> ..." instead of "A -> ...; B -> ..."
 */
public class SetupStatechartTrafos extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    state.getTool().addTrafo(new SimplifyStatechartTrafo());
  }

}
