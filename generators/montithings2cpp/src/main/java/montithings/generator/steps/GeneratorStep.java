// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps;

import montithings.generator.data.GeneratorToolState;

public abstract class GeneratorStep implements GeneratorStepExecution {

  /**
   * The step to be executed after this step
   */
  protected GeneratorStep nextStep;

  /**
   * Executes the current step and triggers the following step
   *
   * @param state State of the Generator before executing the step
   */
  public void execute(GeneratorToolState state) {
    action(state);
    nextStep.execute(state);
  }

  public GeneratorStep setNextStep(GeneratorStep nextStep) {
    this.nextStep = nextStep;
    return this.nextStep;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public GeneratorStep getNextStep() {
    return nextStep;
  }

}
