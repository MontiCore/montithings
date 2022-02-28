// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps;

import montithings.generator.data.GeneratorToolState;

/**
 * Empty Generator step that stops the execution.
 * Can be used as a final step and avoids using "null".
 */
public class StopStep extends GeneratorStep {

  @Override public void action(GeneratorToolState state) {
    // intentionally left blank
  }

  @Override public void execute(GeneratorToolState state) {
    // intentionally left blank
  }

}

