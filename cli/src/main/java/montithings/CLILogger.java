// (c) https://github.com/MontiCore/monticore
package montithings;

import de.se_rwth.commons.logging.Log;

/**
 * Just like Log from SE commons, but hides logName, as that's internal info only
 * and irrelevant to end-users. Also suppresses "package suspected" messages from MontiCore.
 */
public class CLILogger extends Log {

  // show debugging and tracing info?
  protected boolean isDEBUG = true;
  protected boolean isTRACE = true;
  protected boolean isINFO  = true;
  protected boolean isNonZeroExit = true;

  /**
   * Initialize the CLILogger as Log
   */
  public static void init() {
    CLILogger l = new CLILogger();
    l.isTRACE = false;
    l.isDEBUG = false;
    l.isNonZeroExit = true;
    Log.setLog(l);
  }

  @Override protected void doTrace(String msg, String logName) {
    if(doIsTraceEnabled(logName)) {
      doPrint("[TRACE] " + msg);
    }
  }

  @Override protected void doDebug(String msg, String logName) {
    if(doIsDebugEnabled(logName)) {
      doPrint("[DEBUG] " + msg);
    }
  }

  @Override protected void doInfo(String msg, String logName) {
    if (msg.equals("package suspected")) {
      // skip "package suspected" logs
      return;
    }
    if(doIsInfoEnabled(logName)) {
      doPrint("[INFO] " + msg);
    }
  }

  /*
   * The following methods are copied from Log because Log uses isTRACE etc.
   * with package-private visibility - thus, we cannot use them here and overriding them
   * makes no difference.
   */

  /**
   * Is level TRACE enabled for the given log name?
   *
   * @param logName custom name for log, e.g. a method or class name
   * @return whether level TRACE is enabled for the given log name
   */
  @Override protected boolean doIsTraceEnabled(String logName) {
    return isTRACE;
  }

  /**
   * Is level DEBUG enabled for the given log name?
   *
   * @param logName custom name for log, e.g. a method or class name
   * @return whether level DEBUG is enabled for the given log name
   */
  @Override protected boolean doIsDebugEnabled(String logName) {
    return isDEBUG;
  }

  /**
   * Is level INFO enabled for the given log name?
   *
   * @param logName custom name for log, e.g. a method or class name
   * @return whether level INFO is enabled for the given log name
   */
  @Override protected boolean doIsInfoEnabled(String logName) {
    return isINFO;
  }

  /**
   * Is exit with non-zero code enabled?
   */
  @Override protected boolean doIsNonZeroExitEnabled() {
    return isNonZeroExit;
  }

  /*
    This method is copy pasted from Log. If this class does not contain
    it the CLI will print everything into the same line if its build using
    Gradle. This is only a Gradle problem. The CLI built using Maven works
    as intended without this method.
   */
  protected void doPrint(String msg) {
    System.out.println(msg);
  }
}
