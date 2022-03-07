// (c) https://github.com/MontiCore/monticore
package montithings;

import org.apache.commons.cli.Option;

import java.util.HashSet;
import java.util.Set;

public abstract class CLIStep implements CLIStepExecution {

  /**
   * The step to be executed after this step
   */
  protected CLIStep nextStep;

  /**
   * CLI Option required to execute this step
   */
  protected Set<Option> necessaryOptions = new HashSet<>();

  /**
   * Executes the current step and triggers the following step
   * Only execute step if all necessary Options are set
   *
   * @param state State of the Generator before executing the step
   */
  public void execute(CLIState state) {
    boolean shouldExecute = shouldExecute(state);

    if (shouldExecute) {
      action(state);
    }
    nextStep.execute(state);
  }

  protected boolean shouldExecute(CLIState state) {
    boolean shouldExecute = true;

    if (necessaryOptions.contains(CLIOptions.noOption())
      && state.getCmd().getOptions().length != 0) {
      shouldExecute = false;
    }

    if (necessaryOptions.stream()
      .filter(o -> !o.equals(CLIOptions.noOption()))
      .anyMatch(o -> !state.getCmd().hasOption(o.getOpt()))) {
      shouldExecute = false;
    }

    return shouldExecute;
  }

  public CLIStep setNextStep(CLIStep nextStep) {
    this.nextStep = nextStep;
    return this.nextStep;
  }

  public CLIStep onlyIf(Option option) {
    necessaryOptions.add(option);
    return this;
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public CLIStep getNextStep() {
    return nextStep;
  }

}
