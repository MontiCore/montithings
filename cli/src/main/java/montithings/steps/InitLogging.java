// (c) https://github.com/MontiCore/monticore
package montithings.steps;

import de.se_rwth.commons.logging.Log;
import montithings.CLILogger;
import montithings.CLIState;
import montithings.CLIStep;

public class InitLogging extends CLIStep {

  @Override public void action(CLIState state) {
    if (state.getCmd().hasOption("d")) {
      Log.initDEBUG();
    }
    else {
      CLILogger.init();
    }
  }

}
