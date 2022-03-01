// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps;

import montithings.generator.data.GeneratorToolState;

public class ConditionalStop extends GeneratorStep {

  /**
   * If True, all further steps will be skipped
   */
  boolean shouldStop;

  @Override public void action(GeneratorToolState state) {
    // intentially left blank
  }

  @Override public void execute(GeneratorToolState state) {
    if (shouldStop) {
      return;
    }
    super.execute(state);
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public ConditionalStop(boolean shouldStop) {
    this.shouldStop = shouldStop;
  }

}
