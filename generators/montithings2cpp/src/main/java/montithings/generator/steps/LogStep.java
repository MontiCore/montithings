// (c) https://github.com/MontiCore/monticore
package montithings.generator.steps;

import de.se_rwth.commons.logging.Log;
import montithings.generator.data.GeneratorToolState;

/**
 * Step that only prints a log message.
 * Usually to log progress of steps.
 */
public class LogStep extends GeneratorStep {

  protected String message;

  protected String logName;

  @Override public void action(GeneratorToolState state) {
    Log.info(message, logName);
  }

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public LogStep(String message, String logName) {
    this.message = message;
    this.logName = logName;
  }

}
