// (c) https://github.com/MontiCore/monticore
package montithings;

public interface CLIStepExecution {

  /**
   * Execute a step in the CLI process
   *
   * @param state State of the CLI before executing the step
   */
  void action(CLIState state);

}
