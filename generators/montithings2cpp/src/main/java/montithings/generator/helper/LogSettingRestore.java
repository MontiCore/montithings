// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import de.se_rwth.commons.logging.Log;

/**
 * Temporarily save a log to be restored later
 * Only necessary because setLog and getLog are protected
 */
public class LogSettingRestore extends Log {

  Log savedLog;

  public void save() {
    savedLog = Log.getLog();
  }

  public void restore() {
    Log.setLog(savedLog);
  }
}
