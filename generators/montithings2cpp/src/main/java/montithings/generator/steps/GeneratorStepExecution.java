// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps;

import montithings.generator.data.GeneratorToolState;

public interface GeneratorStepExecution {

  /**
   * Execute a step in the code generation process
   *
   * @param state State of the generator before executing the step
   */
  void action(GeneratorToolState state);

}
